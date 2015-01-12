package nosql.workshop.batch.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

/**
 * Importe les données issues des fichiers CSV dans la base MongoDB.
 */
public class CsvToMongoDb {

    public static void main(String[] args) throws UnknownHostException {
        // Création du client Mongo
        MongoClient mongoClient = new MongoClient();

        // Récupération de la collection "installations" de la base "nosql-workshop"
        DB db = mongoClient.getDB("nosql-workshop");
        DBCollection installationsCollection = db.getCollection("installations");

        // Import des données
        new InstallationsImporter(installationsCollection).run();
        new EquipementsImporter(installationsCollection).run();
        new ActivitesImporter(installationsCollection).run();
    }

}
