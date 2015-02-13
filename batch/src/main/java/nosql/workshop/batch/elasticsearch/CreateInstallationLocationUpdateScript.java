package nosql.workshop.batch.elasticsearch;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.net.UnknownHostException;

import static nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils.*;

/**
 * Job qui permet de céer les requêtes d'update pour mongodb des locations géographiques des installations en fonction des rues du pays de loire.
 * Dans l'état, le script d'update apparaît dans la sortie standard :D
 */
public class CreateInstallationLocationUpdateScript {


    public static void main(String[] args) throws UnknownHostException {
        MongoClient mongoClient = null;

        try (Client elasticSearchClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress(ES_DEFAULT_HOST, ES_DEFAULT_PORT));) {
            mongoClient = new MongoClient();

            // cursor all database objects from mongo db
            DBCursor cursor = getMongoCursorToAllInstallations(mongoClient);

            while (cursor.hasNext()) {
                DBObject object = cursor.next();
                String id = (String) object.get("_id");
                String search = (String) ((DBObject) object.get("adresse")).get("voie");
                search += " " + ((DBObject) object.get("adresse")).get("lieuDit");

                // yeark!
                search = search.replaceAll("/", " ");
                search = search.replaceAll("\\^", " ");

                String postCode = (String) ((DBObject) object.get("adresse")).get("codePostal");
                String commune = (String) ((DBObject) object.get("adresse")).get("commune");


                FilteredQueryBuilder builder = QueryBuilders.filteredQuery(QueryBuilders.queryString(search), FilterBuilders.termFilter("postCode", postCode));

                SearchResponse searchResponse = elasticSearchClient.prepareSearch("streets").setTypes("street").setSize(1).setQuery(builder).execute().actionGet();

                // if we did not find anything probing for the street, let's do something with town's location...
                long totalHits = searchResponse.getHits().getTotalHits();
                if (totalHits == 0) {
                    SearchResponse townSearchResponse = elasticSearchClient.prepareSearch("towns").setTypes("town").setSize(1).setQuery(QueryBuilders.matchQuery("townName", commune)).execute().actionGet();
                    if (townSearchResponse.getHits().totalHits() == 0) {
                        System.out.println(postCode + ", " + commune + ", " + search);
                    } else {
                        searchResponse = townSearchResponse;
                    }
                }

                if (searchResponse.getHits().getTotalHits() > 0) {
                    System.out.println("db.installations.update({_id : \"" + id + "\"}, {$set : {\"location.coordinates\" : " + searchResponse.getHits().getAt(0).getSource().get("location") + "}})");
                }
            }
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }

    }
}
