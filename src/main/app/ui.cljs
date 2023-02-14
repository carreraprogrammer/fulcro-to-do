(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [app.application :as application]
    ))

(defsc Todo [this {:todo/keys [id task done]}]
  {:query [:todo/id :todo/task :todo/done]
   :ident :todo/id}
  (dom/li
   (dom/label
    (dom/input {:type    "checkbox"
                :checked done
                :onClick (fn [] (println "DONE: " task))
                })
    (dom/span task))))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defsc TodoList [this]
  {:query [:list/label :list/todos]
   :ident :list/label
   :initial-state (fn [_] {:todos {:list/label "FULCRO TODO" :list/todos
                                   [{:todo/id 1 :todo/task "Do the dishes" :todo/done false}
                                    {:todo/id 2 :todo/task "Buy groceries" :todo/done false}
                                    {:todo/id 3 :todo/task "Take out the trash" :todo/done true}]}})}
  (dom/form
    (dom/h1 (:list/label (:todos this)))
    (dom/div
      (dom/input {:type "text" :placeholder "Add a new task"})
      (dom/input {:type "submit" :value "Add" }))
    (dom/ul (map ui-todo (:list/todos (:todos this))))
    (dom/button "Clear Completed")))


(def ui-list (comp/factory TodoList))

(defsc Root [this {:keys [todos]}]
   {}
    (dom/div (ui-list todos)))


