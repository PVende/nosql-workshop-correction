package nosql.workshop.batch.elasticsearch;

import com.mongodb.*;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import nosql.workshop.connection.ESConnectionUtil;

import java.net.UnknownHostException;
import java.util.stream.StreamSupport;

/**
 * Transferts les documents depuis MongoDB vers Elasticsearch.
 */
public class MongoDbToElasticsearch {

    public static void main(String[] args) throws UnknownHostException {

        MongoClient mongoClient = null;

        JestClient client = ESConnectionUtil.createBonsaiClient();

        long startTime = System.currentTimeMillis();
        try {
            String mongoGivenUri = System.getenv("MONGOLAB_URI");
            String uri = mongoGivenUri == null ? "mongodb://localhost:27017/nosql-workshop" : mongoGivenUri;
            MongoClientURI mongoClientURI = new MongoClientURI(uri);
            mongoClient = new MongoClient(mongoClientURI);

            DBCursor cursor = getMongoCursorToAllInstallations(mongoClient);

            StreamSupport.stream(cursor.spliterator(), false)
                    .forEach((dbObject) -> {
                        indexInstallation(client, dbObject);
                    });

            System.out.println("Inserted all documents in " + (System.currentTimeMillis() - startTime) + " ms");
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    /**
     * Indexation d'une installation
     *
     * @param client   JestClient to handle insertion
     * @param dbObject MongoDB object to insert in ES
     */
    private static void indexInstallation(JestClient client, DBObject dbObject) {
        String objectId = (String) dbObject.get("_id");
        dbObject.removeField("dateMiseAJourFiche");

        Index index = new Index.Builder(dbObject.toString()).index("installations").type("installation").id(objectId).build();
        try {
            client.execute(index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DBCursor getMongoCursorToAllInstallations(MongoClient mongoClient) {
        DB db = mongoClient.getDB("nosql-workshop");
        DBCollection installationsCollection = db.getCollection("installations");

        return installationsCollection.find();
    }

}
