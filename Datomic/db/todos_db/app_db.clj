(ns todos-db.app-db
(:require [datomic.client.api :as d]))

 (def client (d/client {:server-type :dev-local
                       :system "dev"
                       :storage-dir :mem
                       }))
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

 {:db/ident        :list/todos
  :db/valueType    :db.type/ref
  :db/cardinality  :db.cardinality/many
  :db/doc          "The list of todos"}

  {:db/ident       :todo/id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one}

  {:db/ident       :todo/text
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one}

  {:db/ident       :todo/done
   :db/valueType   :db.type/boolean
   :db/cardinality :db.cardinality/one
   :db/doc         "Whether the todo is done or not"}

  {:db/ident       :list-todo
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/many
   :db/doc         "The todos in the list"}])

(d/transact conn {:tx-data todo-schema})

(d/list-databases client {})



