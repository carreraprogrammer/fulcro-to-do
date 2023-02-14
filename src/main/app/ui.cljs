(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Todo [this {:todo/keys [id task done]}]
  (dom/li
   (dom/label
    (dom/input {:type    "checkbox"
                :checked done
                :onClick (fn [] (println "DONE: " task))
                })
    (dom/span task))))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defsc TodoList [this {:list/keys [label todos]}]
  (dom/form
    (dom/h1 label)
    (dom/div
      (dom/input {:type "text" :placeholder "Add a new task"})
      (dom/input {:type "submit" :value "Add"}))
    (dom/ul (map ui-todo todos))))

(def ui-list (comp/factory TodoList))

(defsc Root [this state]
  (let [todo-data {:todos {:list/label "FULCRO TODO" :list/todos
                           [{:todo/id 1 :todo/task "Do the dishes" :todo/done false}
                            {:todo/id 2 :todo/task "Buy groceries" :todo/done false}
                            {:todo/id 3 :todo/task "Take out the trash" :todo/done true}]}}]
    (dom/div (ui-list (:todos todo-data)))))
