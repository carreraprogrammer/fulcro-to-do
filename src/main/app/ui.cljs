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

(defsc TodoList [this {:list/keys [label todos]}]
  {:query [:list/label :list/todos]
   :ident :list/label}
  (dom/form
    (dom/h1 label)
    (dom/div
      (dom/input {:type "text" :placeholder "Add a new task"})
      (dom/input {:type "submit" :value "Add" }))
    (dom/ul (map ui-todo todos))
    (dom/button "Clear Completed")))

(def ui-list (comp/factory TodoList))

(defsc Root [this {:keys [todos]}]
   {}
    (dom/div (ui-list todos)))


