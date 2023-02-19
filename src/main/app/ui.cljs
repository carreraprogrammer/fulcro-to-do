
(ns app.ui
  (:require
    [app.mutations :as api]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(def ^:private id-counter (atom 0))

(defsc Todo [this {:keys [id text done] :as props} {:keys [onDelete toggleDone]}]
  {:query         [:id :text :done]
   :identity      :id
   :initial-state (fn [params]
                    (let [new-id (swap! id-counter inc)]
                      {:id new-id :text (:text params) :done (:done params)}))}
  (dom/li
    (dom/input {:type "checkbox" :checked done :onChange #(toggleDone id)})
    (dom/span text)
    (dom/button {:onClick #(onDelete id)} "X")))

(def ui-todo (comp/factory Todo {:keyfn :id}))
(defsc TodoList [this {:list/keys [id title todos] :as props}]
  {:query  [:list/id :list/title {:list/todos (comp/get-query Todo)}]
   :ident :list/id
   :initial-state (fn [{:keys [id title]}]
                    {:list/id id
                     :list/title title
                     :list/todos [(comp/get-initial-state Todo {:text "Buy groceries" :done true})
                                  (comp/get-initial-state Todo {:text "Walk the dog" :done true})
                                  (comp/get-initial-state Todo {:text "Do laundry" :done false})
                                  (comp/get-initial-state Todo {:text "Take a nap" :done true})
                                  (comp/get-initial-state Todo {:text "Go to dance" :done false})
                                  (comp/get-initial-state Todo {:text "Send email" :done true})
                                  ]})}
  (let [delete-todo (fn [todo-id] (comp/transact! this [(api/delete-todo {:list/id id :todo/id todo-id})]))
        toggle-todo-done (fn [todo-id] (comp/transact! this [(api/toggle-todo-done {:list/id id :todo/id todo-id})]))
        clear-done (fn [] (comp/transact! this [(api/clear-done {:list/id id})]))]
    (dom/div
      (dom/h2 title)
      (dom/input {:type "text" :placeholder "Add a new task" :value ""})
      (dom/input {:type "submit" :value "Add"})
      (dom/ul
        (map #(ui-todo (comp/computed % {:onDelete delete-todo  :toggleDone toggle-todo-done})) todos))
      (dom/button {:onClick #(clear-done id)} "Clear completed"  ))))


(def ui-todo-list (comp/factory TodoList {:key-fn :id}))

  (defsc Root [this {:keys [todos]}]
    {:query         [{:todos (comp/get-query TodoList)}]
     :initial-state (fn [_] {:todos (comp/get-initial-state TodoList {:id 0 :title "FULCRO TODO" :todos []})})}
    (ui-todo-list todos))