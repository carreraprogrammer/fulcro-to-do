
(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Todo [this {:todo/keys [id task done]}]
  (dom/label (
               (dom/input {:type    "checkbox"
                           :checked done
                           :onClick (fn [] println "DONE: " task)
                           })
               (dom/span task)
               )))

(defsc Root [this state]
  (let [todo-data {:todos {:list/label "To-do List" :list/todos
                           [{:todo/id 1 :todo/task "Do the dishes" :todo/done false}
                            {:todo/id 2 :todo/task "Buy groceries" :todo/done false}
                            {:todo/id 3 :todo/task "Take out the trash" :todo/done true}]}}]
    (dom/div "IT'S WORKING!")))