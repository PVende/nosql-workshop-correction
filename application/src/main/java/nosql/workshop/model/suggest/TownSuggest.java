package nosql.workshop.model.suggest;

import java.util.List;

/**
 * Created by Chris on 13/02/15.
 */
public class TownSuggest {
    private String value;
    private Double[] location;

    private TownSuggest() {
    }

    public TownSuggest(String value, List<Double> location) {
        this.value = value;
        this.location = location.toArray(new Double[location.size()]);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }
}
