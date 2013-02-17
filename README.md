freebase-neo4j-importer
=======================

Groovy version of the importer.rb script from p243 of 
[Seven Databases in Seven Weeks](http://pragprog.com/book/rwdata/seven-databases-in-seven-weeks)

In order to use this script, build a copy of of the
[Neo4j Java REST API](https://github.com/neo4j/java-rest-binding)
and install it to your local Maven repository.

Get a copy of [the Freebase perfromances TSV file](http://download.freebase.com/datadumps/latest/browse/film/performance.tsv). 
Place the TSV file into the same directory as pom.xml, and run the following:

```
$ mvn exec:java   # will run the importer
```
