(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]))


;; This atom receives a vector with id's that points the data to the list-table :list/todos keys

(def todos-table
  (atom
   [0]))

;; This is the atom with the information of a defined list

(def list-table
  (atom {:todos {:list/id     :todos
                 :list/title  "My Fulcro Todo"
                 :list/todos  [{:todo/id 0, :todo/text "make website", :todo/done false, :todo/edit? false}]}}))

;; This is a resolver receives as input a :todo/id and returns as output :todo/text and :todo/done

(pc/defresolver todo-resolver [env {:todo/keys [id]}]
                {::pc/input  #{:todo/id}
                 ::pc/output [:todo/text :todo/done :todo/edit?]}
                (let [todos @todos-table]
                  (get todos id)))

;; this resolver takes a :list/id input, checks if the corresponding list exists in list-table, and returns the title of the list along with a sorted list of todo IDs.

(pc/defresolver todo-list-resolver [env {:list/keys [id]}]
                  {::pc/input #{:list/id}
                   ::pc/output [:list/title {:list/todos [:todo/id]}]}
                  (when-let [list (get @list-table id)]
                    (assoc list
                      :list/todos (sort-by :todo/id (:list/todos list)))))


(pc/defresolver main-list-resolver [env input]
                {::pc/output [{:todos [:list/id]}]}
                {:todos {:list/id :todos}})

(def resolvers [todo-resolver todo-list-resolver main-list-resolver])