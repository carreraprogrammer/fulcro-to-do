(ns app.mutations
  (:require
    [app.resolvers :refer [list-table]]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

(def ^:private id-counter (atom 6))

(pc/defmutation add-todo [env {list-id   :list/id
                               todo-text :todo/text}]
                {::pc/sym `add-todo}
                (let [id (swap! id-counter inc)
                      todo {:todo/id id :todo/text todo-text :todo/done false}]
                  (swap! list-table update-in [list-id :list/todos] conj todo)
                  {:todo todo}))

(def mutations [add-todo])