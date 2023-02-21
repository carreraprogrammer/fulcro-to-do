
(ns app.ui
  (:require
    [app.mutations :as api]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))
(def input-value (atom ""))

(defsc Todo [this {:todo/keys [id text done] :as props} {:keys [onDelete toggleDone]}]
  {:query [:todo/id :todo/text :todo/done]}
  (dom/li
    (dom/input {:type "checkbox" :checked done :onChange #(toggleDone id)})
    (dom/span text)
    (dom/button {:onClick #(onDelete id)} "X")))

(def ui-todo (comp/factory Todo {:keyfn :id}))
(defsc TodoList [this {:list/keys [id title todos] :as props}]
  {:query [:list/id :list/title {:list/todos (comp/get-query Todo)}]
   :ident (fn [] [:list/id (:list/id props)])}
  (let [delete-todo (fn [todo-id] (comp/transact! this [(api/delete-todo {:list/id id :todo/id todo-id})]))
        toggle-todo-done (fn [todo-id] (comp/transact! this [(api/toggle-todo-done {:list/id id :todo/id todo-id})]))
        clear-done (fn [id] (comp/transact! this [(api/clear-done {:list/id id})]))
        add-todo (fn [list-id text] (comp/transact! this [(api/add-todo {:list/id list-id :todo/text text})]))]
    (dom/div
      (dom/h2 "FULCRO")
      (dom/form
        {:onSubmit (fn [e]
                     (do (.preventDefault e)
                     (reset! input-value "")))}
       (dom/input {:type "text" :placeholder "Add a new task" :value @input-value :onChange #(reset! input-value (.. % -target -value))})
      (dom/input {:type "submit" :value "Add" :onClick #(add-todo id @input-value)}))
      (dom/ul
        (map #(ui-todo (comp/computed % {:onDelete delete-todo :toggleDone toggle-todo-done})) todos))
      (dom/button {:onClick #(clear-done id)} "Clear completed" ))))


(def ui-todo-list (comp/factory TodoList {:key-fn :id}))

  (defsc Root [this {:keys [todos]}]
    {:query         [{:todos (comp/get-query TodoList)}]
     :initial-state  {}}
    (when todos (ui-todo-list todos)))