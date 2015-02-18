package nosql.workshop.connection;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

/**
 * Utilitaire permettant de gérer la connexion à MongoDB
 */
public abstract class ESConnectionUtil {
    private ESConnectionUtil(){}

    public static JestClient createClient(){
        String esGivenUri = System.getenv("SEARCHBOX_URL");
        String serverUri = esGivenUri == null ? "http://localhost:9200" : esGivenUri;

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(serverUri)
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }
}
