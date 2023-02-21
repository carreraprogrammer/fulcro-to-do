(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]))

(def todos-table
  (atom
   {1 {:todo/id 1 :todo/text "Buy groceries" :todo/done true}
   2 {:todo/id 2 :todo/text "Walk the dog" :todo/done true}
   3 {:todo/id 3 :todo/text "Do laundry" :todo/done false}
   4 {:todo/id 4 :todo/text "Take a nap" :todo/done true}
   5 {:todo/id 5 :todo/text "Go to dance" :todo/done false}
   6 {:todo/id 6 :todo/text "Send email" :todo/done true}}))

(def list-table
  (atom {:todos {:list/id     :todos
               :list/title  "Main List"
               :list/todos  [1 2 3 4 5 6]}}))

(pc/defresolver todo-resolver [env {:todo/keys [id]}]
                {::pc/input  #{:todo/id}
                 ::pc/output [:todo/text :todo/done]}
                (let [todos @todos-table]
                  (get todos id)))

(pc/defresolver todo-list-resolver [env {:list/keys [id]}]
                {::pc/input  #{:list/id}
                 ::pc/output [:list/title {:list/todos [:todo/id]}]}
                (when-let [list (get @list-table id)]
                  (assoc list
                    :list/todos (mapv (fn [id]
                                        (if (number? id)
                                          {:todo/id id}
                                          id))
                                      (:list/todos list)))))

(pc/defresolver main-list-resolver [env input]
                {::pc/output [{:todos [:list/id]}]}
                {:todos {:list/id :todos}})

(def resolvers [todo-resolver todo-list-resolver main-list-resolver])