package nosql.workshop.batch.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import nosql.workshop.connection.ESConnectionUtil;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Job d'import des rues de towns_paysdeloire.csv vers ElasticSearch (/towns/town)
 */
public class ImportTowns {
    public static void main(String[] args) throws IOException {
        JestClient client = ESConnectionUtil.createSearchboxClient();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ImportTowns.class.getResourceAsStream("/batch/csv/towns_paysdeloire.csv")));) {
            reader.lines()
                    .skip(1)
                    .filter(line -> line.length() > 0)
                    .forEach(line -> insertTown(line, client));
        } finally {
            client.shutdownClient();
        }

    }

    private static void insertTown(String line, JestClient client) {
        try {
            line = handleComma(line);

            String[] split = line.split(",");

            String townId = split[0];
            String townName = split[1].replaceAll("\"", "");
            String townSuggest = townName.toLowerCase();
            String postCode = split[3].replaceAll("\"", "");

            Double longitude = Double.valueOf(split[6]);
            Double latitude = Double.valueOf(split[7]);


            XContentBuilder sourceBuilder = jsonBuilder().
                    startObject()
                    .field("townName", townName)
                    .startObject("townNameSuggest")
                    .field("input", townSuggest)
                    .field("output", townSuggest)
                    .startObject("payload")
                    .startArray("location")
                    .value(longitude)
                    .value(latitude)
                    .endArray()
                    .endObject()
                    .endObject()
                    .field("postCode", postCode)
                    .startArray("location")
                    .value(longitude)
                    .value(latitude)
                    .endArray().endObject();

            Index index = new Index.Builder(sourceBuilder.string()).index("towns").type("town").id(townId).build();
            client.execute(index);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String handleComma(String line) {
        Pattern pattern = Pattern.compile("(.*\\d+),(\\d+,\\d+),(\\d+.*)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            line = matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3);
        }
        return line;
    }
}
