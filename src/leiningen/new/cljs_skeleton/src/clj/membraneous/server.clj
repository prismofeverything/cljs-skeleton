(ns {{name}}.server
  (:use [ring.middleware.resource :only (wrap-resource)]
        [ring.middleware.reload :only (wrap-reload)])
  (:require [clojure.edn :as edn] 
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [org.httpkit.server :as httpkit] 
            [polaris.core :as polaris]
            [{{name}}.core :as {{name}}]))

(def clients (atom {}))

(defn index
  [request]
  {:status 200 
   :headers {"Content-Type" "text/html"}
   :body (slurp (io/resource "public/index.html"))})

(defn init-client
  [channel data]
  (httpkit/send! channel (pr-str {:op :init})))

(defn dispatch
  [channel data]
  (condp = (:op data)
    :init (init-client channel data)
    {:op :unsupported}))

(defn send-skeletons
  [channel]
  (fn [skeletons]
    (httpkit/send! channel (pr-str {:op :skeletons :skeletons skeletons}))))

(defn stop-channel
  [clients channel]
  ({{name}}/halt-skeletons (get-in clients [channel :every]))
  (dissoc clients channel))

(defn handler
  [request]
  (httpkit/with-channel request channel
    (httpkit/on-close 
     channel 
     (fn [status] 
       (println "Socket closing:" status)
       (swap! clients stop-channel channel)))
    (if (httpkit/websocket? channel)
      (httpkit/on-receive 
       channel
       (fn [raw]
         (println "Received message:" raw)
         (when-not (get @clients channel)
           (let [every ({{name}}/provide-skeletons (send-skeletons channel) 50)]
             (swap! clients assoc channel {:every every})))
         (let [data (edn/read-string raw)
               response (dispatch channel data)]
           (httpkit/send! channel (pr-str response)))))
      (httpkit/send!
       channel
       (index request)))))

(def routes
  (polaris/build-routes 
   [["/" :index #'index]
    ["/async" :async #'handler]]))

(def app
  (-> (polaris/router routes)
      (wrap-resource "public")
      (wrap-reload)))
   
(defn -main 
  [& args]
  (let [port 19991]
    (httpkit/run-server #'app {:port port})
    ({{name}}/wake-skeletons)
    (println (format "{{name}} started on %s" port))))
