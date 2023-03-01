

(d/q '[:find ?todo-id ?text ?done ?edit
       :keys todo/id todo/text todo/done todo/edit
       :where
       [?e :todo/text ?text]
       [?e :todo/id ?todo-id]
       [?e :todo/done ?done]
       [?e :todo/edit? ?edit]
       ]
     (d/db conn))