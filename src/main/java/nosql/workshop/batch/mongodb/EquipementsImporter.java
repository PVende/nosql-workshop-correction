package nosql.workshop.batch.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.io.*;

public class EquipementsImporter {

    private final DBCollection installationsCollection;

    public EquipementsImporter(DBCollection installationsCollection) {
        this.installationsCollection = installationsCollection;
    }

    public void run() {
        InputStream is = CsvToMongoDb.class.getResourceAsStream("/batch/csv/equipements.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            reader.lines()
                    .skip(1)
                    .filter(line -> line.length() > 0)
                    .forEach(line -> updateInstallation(line));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void updateInstallation(final String line) {
        String[] columns = line.split(",");

        String installationId = columns[2];
        // TODO je mettrais bien en "trou" la construction des requêtes 'searchQuery' et 'updateQuery'
        BasicDBObject searchQuery = new BasicDBObject("_id", installationId);

        DBObject equipement = new BasicDBObject()
                .append("numero", columns[4])
                .append("nom", columns[5])
                .append("type", columns[7])
                .append("famille", columns[9]);

        BasicDBObject updateQuery = new BasicDBObject(
                "$push",
                new BasicDBObject("equipements", equipement)
        );

        installationsCollection.update(searchQuery, updateQuery);
    }
}
