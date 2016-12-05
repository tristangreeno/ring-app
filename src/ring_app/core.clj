(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.format :refer [wrap-restful-format]]))

(defn handler [request]
  (response/ok
    {:result (-> request
                 :params
                 :id)}))

(defn wrap-no-cache
  "A middleware handler that adds behavior to the existing handler."
  [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-formats
  "Middleware handler that converts request into a particular format."
  [handler]
  (wrap-restful-format
    handler
    {:formats [:json-kw :transit-json :transit-msgpack]}))


(defn -main []
  (jetty/run-jetty
    (-> #'handler wrap-no-cache wrap-reload wrap-formats)
    {:port 3000 :join? false}))
