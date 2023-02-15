(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [app.application :as application]))

(defsc Todo [this {:todo/keys [id task done] :as props} {:keys [onDelete]}]
  {:query [:todo/id :todo/task :todo/done]
   :ident :todo/id
   :initial-state (fn [{:keys [id task done]}] {:todo/id (keyword id) :todo/task task :todo/done done})}

  (dom/li
    (dom/label
      (dom/input {:type    "checkbox"
                  :checked done
                  :onClick (fn [] (println "DONE: " task))})
      (dom/span task)
      (dom/button {:onClick #(onDelete task)} "X")
      )))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defsc TodoList [this {:list/keys [label todos]}]
  {:query [:list/label {:list/todos (comp/get-query Todo)}]
   :ident :list/label
   :initial-state (fn [{:keys [label todos]}] {
                                               :list/label label
                                               :list/todos todos})}
  (let [delete-task (fn [task] (println label "asked to delete task" task))]
    (dom/div
      (dom/h1 label)
      (dom/div
        (dom/input {:type "text" :placeholder "Add a new task"})
        (dom/input {:type "submit" :value "Add"}))
      (dom/ul (map (fn [t] (ui-todo (comp/computed t {:onDelete delete-task}))) todos))
      (dom/button "Clear Completed"))))

(def ui-list (comp/factory TodoList))

(defsc Root [this {:keys [todos]}]
  {:initial-state
   (fn [_] {:todos (comp/get-initial-state TodoList {:label "FULCRO TODO"
                                                     :todos [(comp/get-initial-state Todo {:id 1 :task "Do the dishes" :done false})
                                                             (comp/get-initial-state Todo {:id 2 :task "Buy groceries" :done false})
                                                             (comp/get-initial-state Todo {:id 3 :task "Take out the trash" :done true})]})})}
  (dom/div (ui-list todos)))
