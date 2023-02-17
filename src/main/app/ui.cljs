(ns app.ui
  (:require
    [app.mutations :as api]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Todo [this {:keys [id text done]} {:keys [onDelete]}]
  {:query         [:id :text :done]
   :initial-state (fn [{:keys [id text done]}] {:todo/id id :todo/text text :todo/done done})}
  (dom/li
    (dom/input {:type "checkbox" :checked done})
    (dom/span text)
    (dom/button {:onClick #(onDelete id)} "X")))
(def ui-todo (comp/factory Todo {:keyfn :id}))

(defsc TodoList [this {:list/keys [id title todos] :as props}]
  {:query [:list/id :list/title {:list/todos (comp/get-query Todo)}]
   :ident (fn [] [:list/id (:list/id props)])
   :initial-state (fn [{:keys [id title todos]}]
                    {:list/id id
                     :list/title title
                     :list/todos todos})}
  (let [delete-todo (fn [todo-id] (comp/transact! this [(api/delete-todo {:list/id id :todo/id todo-id})]))]
    (dom/div
      (dom/h2 title)
      (dom/input {:type "text" :placeholder "Add a new task"})
      (dom/input {:type "submit" :value "Add"})
      (dom/ul
        (map #(ui-todo (comp/computed % {:onDelete delete-todo})) todos)))))

(def ui-todo-list (comp/factory TodoList))

(defsc Root [this {:keys [todos]}]
  {:query         [{:todos (comp/get-query TodoList)}]
   :initial-state (fn [_] {:todos (comp/get-initial-state TodoList {:id :todos :title "FULCRO TODO" :todos [{:id 0 :text "Buy groceries" :done true}
                                                                                                            {:id 1 :text "Walk the dog" :done true}
                                                                                                            {:id 2 :text "Do laundry" :done false}
                                                                                                            {:id 3 :text "Take a nap" :done true}
                                                                                                            {:id 4 :text "Go to dance" :done false}
                                                                                                            {:id 5 :text "Send email" :done true}
                                                                                                            {:id 6 :text "Study code" :done true}]})})}
  (ui-todo-list todos))