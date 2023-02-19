(ns app.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    ))

(defmutation delete-todo
  "Mutation: Delete the task with `:todo/id` from the list with `:list/id`"
  [{list-id :list/id
    todo-id :todo/id}]
  (action [{:keys [state]}]
          (swap! state update-in [:list/id list-id :list/todos]
                 (fn [todos] (remove #(= (:id %) todo-id) todos)))))

(defmutation toggle-todo-done
  "Mutation: Toggle the `done` state of the task with `:todo/id` in the list with `:list/id`"
  [{list-id :list/id
    todo-id :todo/id}]
  (action [{:keys [state]}]
          (swap! state update-in [:list/id list-id :list/todos]
                 (fn [todos] (map (fn [todo]
                                    (if (= (:id todo) todo-id)
                                      (merge todo {:done (not (:done todo))})
                                      todo))
                                  todos)))))

