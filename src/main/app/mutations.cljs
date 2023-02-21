(ns app.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    ))

(def ^:private id-counter (atom -1))
(defmutation delete-todo
  "Mutation: Delete the task with :todo/id from the list with :list/id and update the :todo/id of the remaining tasks"
  [{list-id :list/id
    todo-id :todo/id}]
  (action [{:keys [state]}]
          (swap! state update-in [:list/id list-id :list/todos]
                 (fn [todos]
                   (let [new-todos (remove #(= (:todo/id %) todo-id) todos)]
                     (map-indexed (fn [idx todo]
                                    (assoc todo :todo/id idx))
                                  new-todos)))))
  (remote [env] true))

(defmutation toggle-todo-done
  "Mutation: Toggle the `done` state of the task with `:todo/id` in the list with `:list/id`"
  [{list-id :list/id
    todo-id :todo/id}]
  (action [{:keys [state]}]
          (swap! state update-in [:list/id list-id :list/todos]
                 (fn [todos] (map (fn [todo]
                                    (if (= (:todo/id todo) todo-id)
                                      (merge todo {:todo/done (not (:todo/done todo))})
                                      todo))
                                  todos))))
  (remote [env] true))
(defmutation clear-done
  "Mutation: Clear all the completed tasks from the list with `:list/id`"
  [{list-id :list/id}]
  (action [{:keys [state]}]
          (swap! state update-in [:list/id list-id :list/todos]
                 (fn [todos] (remove #(get % :todo/done) todos))))
  (remote [env] true))

(defmutation add-todo
  "Mutation: Add a new task with :todo/text to the list with :list/id"
  [{list-id :list/id
    todo-text :todo/text}]
  (action [{:keys [state]}]
          (swap! state update-in [:list/id list-id :list/todos]
                 (fn [todos] (concat todos [{:todo/id (swap! id-counter inc) :todo/text todo-text :todo/done false}])))
          )
  (remote [env] true))
