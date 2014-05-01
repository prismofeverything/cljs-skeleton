(ns leiningen.new.cljs-skeleton
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "cljs-skeleton"))

(defn cljs-skeleton
  "Builds a project with clojurescript, websockets, three.js and skeleton tracking"
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' cljs-skeleton project.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             [".gitignore" (render ".gitignore" data)]
             ["README.md" (render "README.md" data)]
             ["LICENSE" (render "LICENSE" data)]
             ["doc/intro.md" (render "doc/intro.md" data)]
             ["resources/public/index.html" (render "resources/public/index.html" data)]
             ["resources/public/css/{{name}}.css" (render "resources/public/css/membraneous.css" data)]
             ["resources/public/js/lib/OrbitControls.js" (render "resources/public/js/lib/OrbitControls.js" data)]
             ["resources/public/js/lib/three.min.r58.js" (render "resources/public/js/lib/three.min.r58.js" data)]
             ["src/cljx/{{sanitized}}/.gitkeep" (render "src/cljx/membraneous/.gitkeep" data)]
             ["src/cljs/{{sanitized}}/connect.cljs" (render "src/cljs/membraneous/connect.cljs" data)]
             ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/membraneous/core.cljs" data)]
             ["src/cljs/{{sanitized}}/events.cljs" (render "src/cljs/membraneous/events.cljs" data)]
             ["src/cljs/{{sanitized}}/geometry.cljs" (render "src/cljs/membraneous/geometry.cljs" data)]
             ["src/cljs/{{sanitized}}/skeleton.cljs" (render "src/cljs/membraneous/skeleton.cljs" data)]
             ["src/cljs/{{sanitized}}/scene.cljs" (render "src/cljs/membraneous/scene.cljs" data)]
             ["src/clj/{{sanitized}}/core.clj" (render "src/clj/membraneous/core.clj" data)]
             ["src/clj/{{sanitized}}/server.clj" (render "src/clj/membraneous/server.clj" data)]
             ["test/{{sanitized}}/core_test.clj" (render "test/membraneous/core_test.clj" data)]
             ["target/generated/cljs/.gitkeep" (render "target/generated/cljs/.gitkeep" data)]
             ["target/generated/clj/.gitkeep" (render "target/generated/clj/.gitkeep" data)])))
