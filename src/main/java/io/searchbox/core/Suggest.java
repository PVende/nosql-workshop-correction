package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;

import java.util.Map;

/**
 * Created by Chris on 19/02/15.
 */
public class Suggest extends AbstractAction<SuggestResult> {

    private final String suggestQuery;
    private final String suggestName;

    private Suggest(Builder builder){
        super(builder);

        this.suggestQuery = builder.suggestQuery;
        this.suggestName = builder.suggestName;

        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return suggestName;
    }

    @Override
    public String getData(Gson gson) {
        Map rootJson = gson.fromJson(suggestQuery, Map.class);
        return gson.toJson(rootJson);
    }

    @Override
    public SuggestResult createNewElasticSearchResult(String json, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new SuggestResult(gson), json, statusCode, reasonPhrase, gson);
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_suggest");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Suggest, Builder>{
        public final String suggestQuery;
        public final String suggestName;

        public Builder(String suggestQuery, String suggestName) {
            this.suggestQuery = suggestQuery;
            this.suggestName = suggestName;
        }

        @Override
        public Suggest build() {
            return new Suggest(this);
        }
    }
}
