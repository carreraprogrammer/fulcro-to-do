(ns todos-db.schema
  (:require [datomic.client.api :as d]))

(def client (d/client {:server-type :dev-local
                       :system "dev"
                       :storage-dir "/home/asus/DATOMIC/storage"}))

(d/delete-database client {:db-name "todo-app"})
(d/list-databases client {})
(d/create-database client {:db-name "todo-app"})
(def conn (d/connect client {:db-name "todo-app"}))

(def todo-schema
  [{:db/ident       :list/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/many
    :db/unique      :db.unique/identity}

   {:db/ident       :list/title
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :todo/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident       :todo/text
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :todo/done
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc         "Whether the todo is done or not"}
   ])

(d/transact conn {:tx-data todo-schema})

(d/list-databases client {})

;; add a new todo function

(defn insert-todo!
  "Insert one todo"
  [conn {:keys [todo-id text done list-id]}]
  (d/transact conn {:tx-data [[:db/add todo-id :todo/id todo-id]
                              [:db/add todo-id :todo/text text]
                              [:db/add todo-id :todo/done done]
                              [:db/add list-id :list/id list-id]
                              ]}))

;; add my first todo

(insert-todo! conn {:todo-id 0
                    :text "make website"
                    :done true
                    :list-id 0
                    })

(insert-todo! conn {:todo-id 1
                    :text "do the laundry"
                    :done true
                    :list-id 0
                    })

(insert-todo! conn {:todo-id 2
                    :text "Walk the dog"
                    :done true
                    :list-id 0
                    })

(insert-todo! conn {:todo-id 3
                    :text "watch seriesss"
                    :done true
                    :list-id 0
                    })

(d/q '[:find ?e ?todo-id ?text ?done
       :where [?todo-id :todo/text ?text]
       [?todo-id :todo/id ?todo-id]
       [?todo-id :todo/done ?done]
       [?e :list/id _]]
     (d/db conn))


; => [[0 2 "Walk the dog" true] [0 0 "make website" true] [0 1 "do the laundry" true] [0 3 "watch series" true]]






