package nosql.workshop.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import nosql.workshop.model.Installation;
import nosql.workshop.model.suggest.TownSuggest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by Chris on 12/02/15.
 */
public class SearchService {
    public static final String INSTALLATIONS_INDEX = "installations";
    public static final String INSTALLATION_TYPE = "installation";
    public static final String TOWNS_INDEX = "towns";
    private static final String TOWN_TYPE = "town";


    public static final String ES_HOST = "es.host";
    public static final String ES_TRANSPORT_PORT = "es.transport.port";

    final Client elasticSearchClient;
    final ObjectMapper objectMapper;

    @Inject
    public SearchService(@Named(ES_HOST) String host, @Named(ES_TRANSPORT_PORT) int transportPort) {
        elasticSearchClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, transportPort));

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Recherche les installations à l'aide d'une requête full-text
     * @param searchQuery la requête
     * @return la listes de installations
     */
    public List<Installation> search(String searchQuery) {
        SearchResponse searchResponse = elasticSearchClient.prepareSearch(INSTALLATIONS_INDEX)
                .setTypes(INSTALLATION_TYPE)
                .setQuery(QueryBuilders.queryString(searchQuery))
                .execute()
                .actionGet();

        SearchHit[] hits = searchResponse.getHits().getHits();

        return Arrays.stream(hits).map((searchHit) -> {return mapToInstallation(searchHit);})
                .collect(Collectors.toList());
    }

    private Installation mapToInstallation(SearchHit searchHit) {
        try {
            return objectMapper.readValue(searchHit.getSourceAsString(), Installation.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TownSuggest> suggestTownName(String townName){
        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder("complete")
                .text(townName)
                .field("townNameSuggest");

        SuggestResponse suggestResponse = elasticSearchClient
                .prepareSuggest(TOWNS_INDEX)
                .addSuggestion(completionSuggestionBuilder)
                .execute()
                .actionGet();

        return ((CompletionSuggestion)suggestResponse.getSuggest().getSuggestion("complete"))
                .getEntries()
                .stream()
                .flatMap((entry) -> {return entry.getOptions().stream();})
                .map((option) -> {return new TownSuggest(option.getText().string(),(List<Double>)option.getPayloadAsMap().get("location"));})
                .collect(Collectors.toList());
    }

    public Double[] getTownLocation(String townName) {
        SearchResponse searchResponse = elasticSearchClient.prepareSearch(TOWNS_INDEX)
                .setTypes(TOWN_TYPE)
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("townName", townName)))
                .execute()
                .actionGet();


        SearchHits hits = searchResponse.getHits();
        if(hits.getTotalHits() == 0){
            return new Double[0];
        }
        List<Double> location = (List<Double>) hits.getAt(0).getSource().get("location");

        return location.toArray(new Double[location.size()]);
    }
}
