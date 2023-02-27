(ns todos-db.schema
  (:require [datomic.client.api :as d])
  (:import (java.util UUID Date)))

(def client (d/client {:server-type :dev-local
                       :system "dev"
                       :storage-dir "/home/asus/DATOMIC/storage"}))

(d/delete-database client {:db-name "todo-app"})
(d/list-databases client {})
(d/create-database client {:db-name "todo-app"})
(def conn (d/connect client {:db-name "todo-app"}))

(def todo-schema
  [{:db/id ":list/id"
    :db/ident :list/id
    :db/valueType {:db/ident :db.type/long}
    :db/cardinality {:db/ident :db.cardinality/one}
    :db/unique {:db/ident :db.unique/identity}}
   {:db/id ":list/title"
    :db/ident :list/title
    :db/valueType {:db/ident :db.type/string}
    :db/cardinality {:db/ident :db.cardinality/one}}
   {:db/id ":list/todos"
    :db/ident :list/todos
    :db/valueType {:db/ident :db.type/ref}
    :db/cardinality {:db/ident :db.cardinality/many}}
   {:db/id ":todo/id"
    :db/ident :todo/id
    :db/valueType {:db/ident :db.type/long}
    :db/cardinality {:db/ident :db.cardinality/one}
    :db/unique {:db/ident :db.unique/identity}}
   {:db/id ":todo/text"
    :db/ident :todo/text
    :db/valueType {:db/ident :db.type/string}
    :db/cardinality {:db/ident :db.cardinality/one}}
   {:db/id ":todo/done"
    :db/ident :todo/done
    :db/valueType {:db/ident :db.type/boolean}
    :db/cardinality {:db/ident :db.cardinality/one}}
   {:db/id ":list/todos+id"
    :db/ident :list/todos+id
    :db/valueType {:db/ident :db.type/tuple}
    :db/cardinality {:db/ident :db.cardinality/one}
    :db/tupleAttrs [:list/id :todo/id]}
   {:db/id ":list/id+title"
    :db/ident :list/id+title
    :db/valueType {:db/ident :db.type/tuple}
    :db/cardinality {:db/ident :db.cardinality/one}
    :db/unique {:db/ident :db.unique/identity}
    :db/tupleAttrs [:list/id :list/title]}])

(d/transact conn {:tx-data todo-schema})

(defn create-list!

  [conn {:keys [id title]}]

  (d/transact conn {
                    :tx-data [
                              [:db/add id :list/id id]
                              [:db/add id :list/title title]
                              ]
                    })
  )
(create-list! conn {:id 0
                   :title "TODOS"
                    })

(d/q '[:find ?id ?title       :where [?e :list/title ?title] [?e :list/id ?id]]
     (d/db conn))

;=> [[#uuid"9d75b380-358c-4440-9abe-e25e9a95529a" "TODOS"] [#uuid"6413c235-e963-4815-a766-d72836ed0470" "TODOS 2"]]

(defn upsert-todo!
  "Insert one todo"
  [conn {:keys [todo-id text done list-id title]}]
  (d/transact conn {:tx-data [[:db/add "temporary-new-db-id" :todo/id todo-id]
                              [:db/add "temporary-new-db-id" :todo/text text]
                              [:db/add "temporary-new-db-id" :todo/done done]
                              [:db/add 0 :list/todos+id [list-id todo-id]]
                              [:db/add 0 :list/id+title [list-id title]]
                              ]}))

;; add my first todo

(upsert-todo! conn {:todo-id 0
                    :text "make website"
                    :done false
                    :list-id 0
                    :title "TODOS"
                    })

(upsert-todo! conn {:todo-id 1
                    :text "do the laundry"
                    :done true
                    :list-id 0
                    :title "TODOS"
                    })

(upsert-todo! conn {:todo-id 2
                    :text "Walk the dog"
                    :done false
                    :list-id 0
                    :title "TODOS"
                    })

(upsert-todo! conn {:todo-id 4
                    :text "clean my bedroom"
                    :done false
                    :list-id 0
                    :title "TODOS"
                    })

(upsert-todo! conn {:todo-id 5
                    :text "watch series"
                    :done true
                    :list-id 0
                    :title "TODOS"
                    })

(d/q '[:find ?todo-id ?text ?done
       :keys todo/id todo/text todo/done
       :where
       [?e :todo/text ?text]
       [?e :todo/id ?todo-id]
       [?e :todo/done ?done]
     ]
     (d/db conn))

;=>=>
;[["TODOS" 4 "clean my bedroom" true]
; ["TODOS" 0 "make website" true]
; ["TODOS" 5 "watch series" true]
; ["TODOS" 2 "Walk the dog" true]
; ["TODOS" 1 "do the laundry" true]]

(defn retract-todo!
  "Retract all the fields based on the :movie/id"
  [conn id]
  (try
    (d/transact conn {:tx-data
                      [[:db/retract [:todo/id id] :todo/id ]
                       [:db/retract[:todo/id id] :todo/text]
                       [:db/retract [:todo/id id] :todo/done]
                      ]})
    (catch Exception e "Nothing was deleted")))

(retract-todo! conn 5)

;; [["TODOS" 4 "clean my bedroom" true]
; ["TODOS" 0 "make website" true]
; ["TODOS" 2 "Walk the dog" true]
; ["TODOS" 1 "do the laundry" true]]

(d/q '[:find ?list-title ?todo-id ?text
       :where
       [?e :todo/text ?text]
       [?e :todo/id ?todo-id]
       [?e :todo/done false]
       [_ :list/title ?list-title]
       ]
     (d/db conn))

; => => [["TODOS" 4 "clean my bedroom"] ["TODOS" 2 "Walk the dog"] ["TODOS" 0 "make website"]]
