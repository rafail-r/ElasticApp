package gr.ntua.ece.elasticapp.elasticapp;

/**
 * Created by rafail on 19/7/2016.
 */
public class Place {
    private String name;
    private String id;
    private String type;
    private Float rating;
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = Float.parseFloat(rating);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
