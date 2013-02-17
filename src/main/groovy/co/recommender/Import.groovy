package co.recommender

import groovy.transform.TypeChecked
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.index.IndexHits
import org.neo4j.rest.graphdb.RestAPIFacade
import org.neo4j.rest.graphdb.RestGraphDatabase

/**
* Example Groovy class.
*/
class Import {
    //@TypeChecked  // see http://docs.codehaus.org/display/GROOVY/Groovy-Eclipse+2.7.0+New+and+Noteworthy
    def org.neo4j.graphdb.Node getOrCreateNode(GraphDatabaseService gds, String indexName, String value) {
        org.neo4j.graphdb.index.Index<org.neo4j.graphdb.Node> idx = gds.index().forNodes(indexName)
        org.neo4j.graphdb.index.IndexHits<org.neo4j.graphdb.Node> hits = idx.get("name", value)
        org.neo4j.graphdb.Node node = hits.getSingle()
        if (node==null) {
            node = gds.createNode();

            // index name is plural, so strip off the 's' at the end
            def typeStr = indexName.substring(0, indexName.length()-1)

            node.setProperty("type", typeStr)
            node.setProperty("name", value)

            println("****\tCreating [$typeStr] node: $value")
            idx.add(node, "name", node.getProperty("name"))
        }
        return node
    }

    enum RelTypes implements RelationshipType {
        ACTED_IN
    }

    def ImportPerformanceTSV() {
        def fileName = "performance.tsv"
        println "Importing $fileName"

        // http://api.neo4j.org/1.8.M07/org/neo4j/graphdb/GraphDatabaseService.html
        GraphDatabaseService gds = new RestGraphDatabase("http://localhost:7474/db/data")

        int lineNumber = 0
        new File(fileName).eachLine { line ->
            lineNumber++
            if (lineNumber == 1) {
                return // we skip the first line because we don't need the header
            }
            //else if (lineNumber==8743) {
            //    println("break here")
            //}
            try {
                def (name, id, actor, movie) = line.split("\t")
                println("$lineNumber:\t$actor --ACTED_IN--> $movie")
                def actorNode = getOrCreateNode(gds, 'actors', actor)
                def movieNode = getOrCreateNode(gds, 'movies', movie)

                actorNode.createRelationshipTo(movieNode, RelTypes.ACTED_IN)
            }
            catch (e) {
                println(e.getMessage())
            }
        }
    }

    def static void main(String[]  args) {
        (new Import()).ImportPerformanceTSV()
    }
}