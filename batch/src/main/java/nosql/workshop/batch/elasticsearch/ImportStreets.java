package nosql.workshop.batch.elasticsearch;

import nosql.workshop.batch.elasticsearch.util.BulkHandler;
import nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

import static nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils.*;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Job d'import des rues de streets_paysdeloire.csv vers ElasticSearch (/streets/street)
 */
public class ImportStreets {
    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ImportTowns.class.getResourceAsStream("/csv/streets_paysdeloire.csv")));
             Client elasticSearchClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress(ES_DEFAULT_HOST, ES_DEFAULT_PORT));) {

            checkIndexExists("streets", elasticSearchClient);

            BulkHandler bulkHandler = new BulkHandler(elasticSearchClient);

            reader.lines()
                    .skip(1)
                    .filter(line -> line.length() > 0)
                    .forEach(line -> insertStreets(line, bulkHandler));

            BulkResponse bulkResponse = bulkHandler.getBulkRequest().execute().actionGet();

            dealWithFailures(bulkResponse);
        }

    }

    private static void insertStreets(String line, BulkHandler bulkHandler) {
        try {
            line = ElasticSearchBatchUtils.handleComma(line);


            String[] split = line.split(",");

            String streetName = split[2].replaceAll("\"", "");
            XContentBuilder sourceBuilder = jsonBuilder().
                    startObject()
                    .field("steetName", streetName)
                    .field("townName", split[3].replaceAll("\"", ""))
                    .field("postCode", split[4].replaceAll("\"", ""))
                    .startArray("location")
                        .value(Double.valueOf(split[5]))
                        .value(Double.valueOf(split[6]))
                    .endArray().endObject();

            bulkHandler.getBulkRequest().add(bulkHandler.elasticSearchClient.prepareIndex("streets", "street", split[0]).setSource(sourceBuilder));

            bulkHandler.increment();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
