(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]))

(def todos-table
  (atom
   {}))

(def list-table
  (atom {:todos {:list/id     :todos
               :list/title  "Main List"
               :list/todos  []}}))

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
                    :list/todos (map (fn [id]
                                        (if (number? id)
                                          {:todo/id id}
                                          id))
                                      (:list/todos list)))))

(pc/defresolver main-list-resolver [env input]
                {::pc/output [{:todos [:list/id]}]}
                {:todos {:list/id :todos}})

(def resolvers [todo-resolver todo-list-resolver main-list-resolver])