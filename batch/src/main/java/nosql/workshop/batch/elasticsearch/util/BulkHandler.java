package nosql.workshop.batch.elasticsearch.util;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import static nosql.workshop.batch.elasticsearch.util.ElasticSearchBatchUtils.dealWithFailures;

/**
* Created by Chris on 13/02/15.
*/
public class BulkHandler {
    public static final int BULK_SIZE = 10000;
    public final Client elasticSearchClient;
    private int count = 0;
    private BulkRequestBuilder bulkRequest;


    public BulkHandler(Client elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
        prepareBulk();
    }

    private void prepareBulk(){
        bulkRequest = elasticSearchClient.prepareBulk();
    }

    public void increment(){
        if(++count % BULK_SIZE == 0){
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();

            dealWithFailures(bulkResponse);

            prepareBulk();
        }
    }

    public BulkRequestBuilder getBulkRequest(){
        return bulkRequest;
    }
}
