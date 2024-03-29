
(ns app.ui
  (:require
    [app.mutations :as api]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))
(def input-value (atom ""))

(defsc Todo [this {:todo/keys [id text done edit?] :as props} {:keys [onDelete toggleDone toggleEdit editTodo]}]
  {:query [:todo/id :todo/text :todo/done :todo/edit?]}
  (let [text-value (atom text)
        on-input-change (fn [e] (reset! text-value (.. e -target -value)))]
    (dom/tr
      (dom/td :.td-id (str id))
      (if edit?
        (dom/td :.td-todo
                (dom/form :.edit-form
                          {:onSubmit (fn [e]
                                       (do (.preventDefault e)
                                           (editTodo id @text-value)
                                           (reset! text-value "")))}
                          (dom/input :.edit-text {:autoFocus true :type "text" :value @text-value :onChange on-input-change})
                          (dom/input :.save-btn {:type "submit" :value "Save" :onClick #(do (editTodo id @text-value) (toggleEdit id))})
                          ))
        (dom/td :.td-todo
                (dom/div :.todo-container (dom/input :.checkbox {:type "checkbox" :checked done :onChange #(toggleDone id)})
                (dom/div {:onClick #(toggleEdit id) } text))
                ))
      (dom/td :.td-menu (dom/div :.menu {}
                                 (dom/i {:class "fa-regular fa-pen-to-square edit-btn" :onClick #(toggleEdit id) })
                                 (dom/button :.x-btn {:onClick #(onDelete id)} (dom/div :.x-one) (dom/div :.x-two)) )))))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defsc TodoList [this {:list/keys [id title todos] :as props}]
  {:query [:list/id :list/title {:list/todos (comp/get-query Todo)}]
   :ident (fn [] [:list/id (:list/id props)])}
  (let [delete-todo (fn [todo-id] (comp/transact! this [(api/delete-todo {:list/id id :todo/id todo-id})]))
        toggle-todo-done (fn [todo-id] (comp/transact! this [(api/toggle-todo-done {:list/id id :todo/id todo-id})]))
        clear-done (fn [id] (comp/transact! this [(api/clear-done {:list/id id})]))
        add-todo (fn [list-id text] (comp/transact! this [(api/add-todo {:list/id list-id :todo/text text})]))
        toggle-todo-edit (fn [todo-id] (comp/transact! this [(api/toggle-todo-edit {:list/id id :todo/id todo-id})]))
        edit-todo (fn [todo-id todo-text] (comp/transact! this [(api/edit-todo {:list/id id :todo/id todo-id :todo/text todo-text})]))]
    (dom/div :.todos-container
             (dom/h2 :.todos-title title)
             (dom/form :.input-form
                       {:onSubmit (fn [e]
                                    (do (.preventDefault e)
                                        (reset! input-value "")))}
                       (dom/input :.input {:type "text" :placeholder "Add a new task" :value @input-value :onChange #(reset! input-value (.. % -target -value))})
                       (dom/input :.add-btn {:type "submit" :value "Add" :onClick #(when (not (empty? @input-value)) (add-todo id @input-value))}))
             ( when (> (count todos) 0)
              (dom/table
               (dom/thead
                 (dom/tr
                   (dom/th :.th-id "ID")
                   (dom/th :.th-todo "Todo")
                   (dom/th :.th-options "Options")))
               (dom/tbody
                 (map #(ui-todo (comp/computed % {:onDelete delete-todo :toggleDone toggle-todo-done :toggleEdit toggle-todo-edit :editTodo edit-todo})) todos))))
             (when (> (count todos) 0)
              (dom/button :.clear-btn {:onClick #(clear-done id)} "Clear Completed" )))))


(def ui-todo-list (comp/factory TodoList {:key-fn :id}))

  (defsc Root [this {:keys [todos]}]
    {:query         [{:todos (comp/get-query TodoList)}]
     :initial-state  {}}
    (when todos (ui-todo-list todos)))