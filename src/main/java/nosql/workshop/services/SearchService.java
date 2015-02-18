package nosql.workshop.services;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import nosql.workshop.connection.ESConnectionUtil;
import nosql.workshop.model.Installation;
import nosql.workshop.model.suggest.TownSuggest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Chris on 12/02/15.
 */
public class SearchService {
    public static final String INSTALLATIONS_INDEX = "installations";
    public static final String INSTALLATION_TYPE = "installation";
    public static final String TOWNS_INDEX = "towns";
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
     * TODO pour l'instant très vilain (basé sur une prefix query, merci Jest :D)
     */
    public List<TownSuggest> suggestTownName(String townName) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.prefixQuery("townName", townName));

        try {
            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex(TOWNS_INDEX)
                    .addType(TOWN_TYPE)
                    .build();

            SearchResult result = searchboxClient.execute(search);

            return result.getHits(TownSuggest.class).stream()
                    .map((hit) -> hit.source)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
