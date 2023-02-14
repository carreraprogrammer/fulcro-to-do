
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

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defsc Todo-list [this {:list/keys [label todos]}]
  (dom/div
    ((dom/h1 label)
     (dom/ul todos))))

(defsc Root [this state]
  (let [todo-data {:todos {:list/label "FULCRO TODO" :list/todos
                           [{:todo/id 1 :todo/task "Do the dishes" :todo/done false}
                            {:todo/id 2 :todo/task "Buy groceries" :todo/done false}
                            {:todo/id 3 :todo/task "Take out the trash" :todo/done true}]}}]
    (dom/div "IT'S WORKING!")))