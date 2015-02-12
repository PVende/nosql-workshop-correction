package nosql.workshop.batch.elasticsearch;

import com.mongodb.*;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;

/**
 * Transferts les documents depuis MongoDB vers Elasticsearch.
 */
public class MongoDbToElasticsearch {

    public static void main(String[] args) {

        MongoClient mongoClient = null;
        Client elasticSearchClient = null;

        long startTime = System.currentTimeMillis();
        try {
            mongoClient = new MongoClient();
            elasticSearchClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

            // cursor all database objects from mongo db
            DBCursor cursor = getMongoCursorToAllInstallations(mongoClient);

            // prepare bulk insert to Elastic Search
            BulkRequestBuilder bulkRequest = elasticSearchClient.prepareBulk();
            while (cursor.hasNext()) {
                DBObject object = cursor.next();

                String objectId = (String) object.get("_id");
                object.removeField("dateMiseAJourFiche");
                bulkRequest.add(elasticSearchClient.prepareIndex("installations", "installation", objectId).setSource(object.toString()));
            }
            BulkResponse bulkItemResponses = bulkRequest.execute().actionGet();

            dealWithFailures(bulkItemResponses);

            System.out.println("Inserted all documents in " + (System.currentTimeMillis() - startTime) + " ms");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
            if (elasticSearchClient != null) {
                elasticSearchClient.close();
            }
        }


    }

    private static void dealWithFailures(BulkResponse bulkItemResponses) {
        if (bulkItemResponses.hasFailures()) {
            System.out.println("Bulk insert has failures : ");
            BulkItemResponse[] items = bulkItemResponses.getItems();
            for (BulkItemResponse bulkItemResponse : items) {
                System.out.println(bulkItemResponse.getFailure());
            }
        }
    }

    private static DBCursor getMongoCursorToAllInstallations(MongoClient mongoClient) {
        DB db = mongoClient.getDB("nosql-workshop");
        DBCollection installationsCollection = db.getCollection("installations");

        return installationsCollection.find();
    }

}
