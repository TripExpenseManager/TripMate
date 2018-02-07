package android.app.models;

/**
 * Created by Sai Krishna on 6/17/2017.
 */

public class TripModel {

    private String trip_name;
    private String trip_date;
    private Double trip_amount;
    private String trip_places;
    private String trip_id;
    private  String imageUrl = "";
    private int total_persons;
    private String tripcurrency;



    public TripModel() {
    }


    public TripModel(String trip_name,String trip_places, String trip_date) {
        this.trip_name = trip_name;
        this.trip_date = trip_date;
        this.trip_places = trip_places;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_places() {
        return trip_places;
    }

    public void setTrip_places(String trip_places) {
        this.trip_places = trip_places;
    }


    public String getTrip_name() {
        return trip_name;
    }

    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }

    public String getTrip_date() {
        return trip_date;
    }

    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }

    public Double getTrip_amount() {
        return trip_amount;
    }

    public void setTrip_amount(Double trip_amount) {
        this.trip_amount = trip_amount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTotal_persons() {
        return total_persons;
    }

    public void setTotal_persons(int total_persons) {
        this.total_persons = total_persons;
    }


    public String getTripcurrency() {
        return tripcurrency;
    }

    public void setTripcurrency(String tripcurrency) {
        this.tripcurrency = tripcurrency;
    }
}
