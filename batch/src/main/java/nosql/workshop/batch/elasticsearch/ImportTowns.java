package nosql.workshop.batch.elasticsearch;

import nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

import static nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils.checkIndexExists;
import static nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils.dealWithFailures;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Job d'import des rues de towns_paysdeloire.csv vers ElasticSearch (/towns/town)
 */
public class ImportTowns {
    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ImportTowns.class.getResourceAsStream("/csv/towns_paysdeloire.csv")));
             Client elasticSearchClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));) {

            checkIndexExists("towns", elasticSearchClient);

            BulkRequestBuilder bulkRequest = elasticSearchClient.prepareBulk();

            reader.lines()
                    .skip(1)
                    .filter(line -> line.length() > 0)
                    .forEach(line -> insertTown(line, bulkRequest, elasticSearchClient));

            BulkResponse bulkItemResponses = bulkRequest.execute().actionGet();

            dealWithFailures(bulkItemResponses);
        }

    }

    private static void insertTown(String line, BulkRequestBuilder bulkRequest, Client elasticSearchClient) {
        try {
            line = ElasticSearchBatchUtils.handleComma(line);

            String[] split = line.split(",");

            String townName = split[1].replaceAll("\"", "");
            XContentBuilder sourceBuilder = jsonBuilder().
                    startObject()
                        .field("townName", townName)
                        .startObject("townNameSuggest")
                            .field("input", townName.toLowerCase())
                            .field("output", townName.toLowerCase())
                            .startObject("payload")
                                .startArray("location")
                                    .value(Double.valueOf(split[6]))
                                    .value(Double.valueOf(split[7]))
                                .endArray()
                            .endObject()
                        .endObject()
                        .field("postCode", split[3].replaceAll("\"", ""))
                        .startArray("location")
                            .value(Double.valueOf(split[6]))
                            .value(Double.valueOf(split[7]))
                        .endArray().endObject();

            bulkRequest.add(
                    elasticSearchClient.prepareIndex("towns", "town", split[0]).setSource(sourceBuilder));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
