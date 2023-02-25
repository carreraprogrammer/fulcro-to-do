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
    :db/cardinality :db.cardinality/one
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

   {:db/ident       :list/todos
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc         "The lists that this todo belongs to"}])

(d/transact conn {:tx-data todo-schema})

(d/list-databases client {})

;; add a new todo function

(defn upsert-todo!
  "Update or insert one record"
  [conn {:keys [todo-id text done list-id]}]
  (d/transact conn {:tx-data [[:db/add "temporary-new-db-id" :todo/id todo-id]
                              [:db/add "temporary-new-db-id" :todo/text text]
                              [:db/add "temporary-new-db-id" :todo/done done]
                              [:db/add "temporary-new-db-id" :list/id list-id]
                              ]}))

;; add my first todo

(upsert-todo! conn {:todo-id 5
                    :text "take a nap"
                    :done false
                    :list-id 0})

(d/q '[:find ?text ?done
       :where [?todo :todo/text ?text]
       [?todo :todo/done ?done]]
     (d/db conn))




