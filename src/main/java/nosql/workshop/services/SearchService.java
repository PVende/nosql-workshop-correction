package nosql.workshop.services;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Suggest;
import io.searchbox.core.SuggestResult;
import nosql.workshop.connection.ESConnectionUtil;
import nosql.workshop.model.Installation;
import nosql.workshop.model.suggest.TownSuggest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search service permet d'encapsuler les appels vers ElasticSearch
 */
public class SearchService {
    private static final String INSTALLATIONS_INDEX = "installations";
    private static final String INSTALLATION_TYPE = "installation";
    private static final String TOWNS_INDEX = "towns";
    private static final String TOWN_TYPE = "town";

    private final JestClient searchboxClient;
    private final JestClient bonsaiClient;

    public SearchService() {
        searchboxClient = ESConnectionUtil.createSearchboxClient();
        bonsaiClient = ESConnectionUtil.createBonsaiClient();
    }

    /**
     * Recherche les installations à l'aide d'une requête full-text
     *
     * @param searchQuery la requête
     * @return la listes de installations
     */
    public List<Installation> search(String searchQuery) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryString(searchQuery));

        try {
            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex(INSTALLATIONS_INDEX)
                    .addType(INSTALLATION_TYPE)
                    .build();


            SearchResult result = bonsaiClient.execute(search);
            return result.getHits(Installation.class).stream()
                    .map((hit) -> hit.source)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Suggestion sur les noms des villes
     *
     * @param townName ville recherchée
     * @return une liste de suggestions
     * <p/>
     */
    public List<TownSuggest> suggestTownName(String townName) {
        String suggestName = "complete";
        String fieldName = "townNameSuggest";

        Suggest towns = buildSuggestRequest(townName, suggestName, fieldName);

        try {
            SuggestResult suggestResult = bonsaiClient.execute(towns);

            List<SuggestResult.SuggestWithPayLoad<TownSuggest>> suggestsWithPayLoad = suggestResult.getSuggestsWithPayLoad(TownSuggest.class);
            suggestsWithPayLoad
                    .forEach((suggest) -> {
                        suggest.paylod.setTownName(suggest.suggest);
                    });

            return suggestsWithPayLoad.stream()
                    .map((suggest) -> {
                        return suggest.paylod;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Création d'une requête de suggestion simple
     * @param suggestQuery comptenue de la suggestion
     * @param suggestName nom de la suggestion (pour l'analyse du retour)
     * @param fieldName nom du champ sur lequel porte la requête
     * @return La Suggest prête à être exécutée
     */
    private Suggest buildSuggestRequest(String suggestQuery, String suggestName, String fieldName) {
        Suggest towns;
        try {
            XContentBuilder sourceBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                        .startObject(suggestName)
                            .field("text", suggestQuery)
                            .startObject("completion")
                            .field("field", fieldName)
                            .endObject()
                        .endObject()
                    .endObject();

            towns = new Suggest.Builder(sourceBuilder.string(), suggestName)
                    .addIndex("towns")
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return towns;
    }

    /**
     * Retourne la location [lon,lat] de la ville d'une ville recherchée par son nom
     *
     * @param townName ville recherchée
     * @return la location associée,
     */
    public Double[] getTownLocation(String townName) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("townName", townName));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(TOWNS_INDEX)
                .addType(TOWN_TYPE)
                .build();

        try {
            SearchResult result = searchboxClient.execute(search);
            SearchResult.Hit<TownSuggest, Void> firstHit = result.getFirstHit(TownSuggest.class);
            if (firstHit != null) {
                return firstHit.source.getLocation();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
