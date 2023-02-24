(ns todos-db.db
  (:require [datomic.api :as d]))

(def client (d/client {:server-type :dev-local
                       :system "dev"
                       :sotrage-dir "home/asus/DATOMIC/storage\n"
                       }))

(defn create-database []
  (d/create-database client {:db-name "todo-app"}))

(def conn (d/connect client {:db-name "todo-app"}))

(def todo-schema [{:list/id {:db/ident       :list/id
                             :db/valueType   :db.type/string
                             :db/cardinality :db.cardinality/one}

                   :list/title {:db/ident       :list/title
                                :db/valueType   :db.type/string
                                :db/cardinality :db.cardinality/one}

                   :list/todos {:db/ident        :list/todos
                                :db/valueType    :db.type/ref
                                :db/cardinality  :db.cardinality/many
                                :db/doc          "The list of todos"
                                :db.install/_ref :list/id}

                   :todo/id {:db/ident       :todo/id
                             :db/valueType   :db.type/string
                             :db/cardinality :db.cardinality/one}

                   :todo/text {:db/ident       :todo/text
                               :db/valueType   :db.type/string
                               :db/cardinality :db.cardinality/one}

                   :todo/done {:db/ident       :todo/done
                               :db/valueType   :db.type/boolean
                               :db/cardinality :db.cardinality/one
                               :db/doc         "Whether the todo is done or not"}

                   :list-todo {:db/ident       :list-todo
                               :db/valueType   :db.type/ref
                               :db/cardinality :db.cardinality/many
                               :db/doc         "The todos in the list"
                               :db.install/_ref :list/id
                               :db.install/_ref :todo/id}}])


(d/transact conn {:tx-data todo-schema})

(defn delete-database []
  (d/delete-database client {:db-name "todo-app"}))

(defn list-databases []
  (d/list-databases client {}))