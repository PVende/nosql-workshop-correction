package nosql.workshop.batch.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

/**
 * Importe les 'installations' dans MongoDB.
 */
public class InstallationsImporter {

    private final DBCollection installationsCollection;

    public InstallationsImporter(DBCollection installationsCollection) {
        this.installationsCollection = installationsCollection;
    }

    public void run() {
        InputStream is = CsvToMongoDb.class.getResourceAsStream("/batch/csv/installations.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            reader.lines()
                    .skip(1)
                    .filter(line -> line.length() > 0)
                    .forEach(line -> installationsCollection.save(toDbObject(line)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private DBObject toDbObject(final String line) {
        String[] columns = line
                .substring(1, line.length() - 1)
                .split("\",\"");

        // TODO je mettrais bien en "trou" toute la construction du document.
        return new BasicDBObject()
                .append("_id", columns[1])
                .append("version", "1") // TODO point important : la version du document.
                .append("nom", columns[0])
                .append(
                        "adresse",
                        new BasicDBObject()
                                .append("numero", columns[6])
                                .append("voie", columns[7])
                                .append("lieuDit", columns[5])
                                .append("codePostal", columns[4])
                                .append("commune", columns[2])
                )
                .append(
                        "location",
                        new BasicDBObject("type", "Point")
                                .append(
                                        "coordinates",
                                        Arrays.asList(
                                                Double.valueOf(columns[9]),
                                                Double.valueOf(columns[10])
                                        )
                                )
                )
                .append("multiCommune", "Oui".equals(columns[16]))
                .append("nbPlacesParking", columns[17].isEmpty() ? null : Integer.valueOf(columns[17]))
                .append("nbPlacesParkingHandicapes", columns[18].isEmpty() ? null : Integer.valueOf(columns[18]))
                .append(
                        "dateMiseAJourFiche",
                        columns.length < 29 || columns[28].isEmpty()
                                ? null :
                                Date.from(
                                        LocalDate.parse(columns[28].substring(0, 10))
                                                .atStartOfDay(ZoneId.of("UTC"))
                                                .toInstant()
                                )
                );
    }
}
