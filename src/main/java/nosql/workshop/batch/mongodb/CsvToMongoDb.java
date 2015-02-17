package nosql.workshop.batch.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Importe les données issues des fichiers CSV dans la base MongoDB.
 */
public class CsvToMongoDb {

    public static void main(String[] args) {
        MongoClient mongoClient = null;
        try {
            // Création du client Mongo
            String givenUri = System.getenv("MONGOLAB_URI");
            String uri = givenUri == null ? "mongodb://localhost:27017/nosql-workshop" : givenUri;
            MongoClientURI mongoClientURI = new MongoClientURI(uri);
            mongoClient = new MongoClient(mongoClientURI);

            // Récupération de la collection "installations" de la base "nosql-workshop"
            DB db = mongoClient.getDB(mongoClientURI.getDatabase());
            DBCollection installationsCollection = db.getCollection("installations");

            // Import des données
            new InstallationsImporter(installationsCollection).run();
            new EquipementsImporter(installationsCollection).run();
            new ActivitesImporter(installationsCollection).run();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

}
