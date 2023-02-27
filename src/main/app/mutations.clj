(ns app.mutations
  (:require
    [app.resolvers :refer [list-table]]
    [com.wsscode.pathom.connect :as pc]))

(pc/defmutation delete-todo [env {list-id :list/id
                                  todo-id :todo/id}]
                {::pc/sym `delete-todo}
                (letfn [(remove-todo [todos]
                          (let [new-todos (remove #(= (:todo/id %) todo-id) todos)]
                            (map-indexed
                              (fn [idx todo]
                                (assoc todo :todo/id idx))
                              new-todos)))]
                  (swap! list-table update-in [list-id :list/todos] remove-todo)
                  {:deleted-todo-id todo-id}))

(pc/defmutation toggle-todo-done [env {list-id :list/id
                                       todo-id :todo/id}]
                {::pc/sym `toggle-todo-done}
                (letfn [(toggle-done [todos]
                          (map (fn [todo]
                                 (if (= (:todo/id todo) todo-id)
                                   (merge todo {:todo/done (not (:todo/done todo))})
                                   todo))
                               todos))]
                  (swap! list-table update-in [list-id :list/todos] toggle-done)
                  {:toggled-todo-id todo-id}))

(pc/defmutation clear-done [env {list-id :list/id}]
                {::pc/sym `clear-done}
                (letfn [(remove-done [todos]
                          (let [new-todos (remove #(get % :todo/done) todos)]
                            (map-indexed
                              (fn [idx todo]
                                (assoc todo :todo/id idx))
                              new-todos)))
                        ]
                  (swap! list-table update-in [list-id :list/todos] remove-done)
                  true))

(pc/defmutation add-todo [env {list-id :list/id
                               todo-text :todo/text}]
                {::pc/sym `add-todo}
                (let [todos (get-in @list-table [list-id :list/todos])
                      id (count todos)
                      todo {:todo/id id :todo/text todo-text :todo/done false :todo/edit? false}]
                  (swap! list-table update-in [list-id :list/todos] conj todo)
                  {:todo/id id :todo/text todo-text :todo/done false :todo/edit? false}))



(def mutations [add-todo delete-todo toggle-todo-done clear-done])