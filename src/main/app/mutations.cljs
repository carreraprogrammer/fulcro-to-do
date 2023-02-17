(ns app.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-todo
  "Mutation: Delete the task with `:todo/id` from the list with `:list/id`"
  [{list-id :list/id
    todo-id :todo/id}]
  (action [{:keys [state]}]
          (swap! state update-in [:list/id list-id :list/todos]
                 (fn [todos] (remove #(= (:id %) todo-id) todos)))))

