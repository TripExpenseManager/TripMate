package com.tripmate;

/**
 * Created by Sai Krishna on 6/17/2017.
 */

public class TripModel {

    private String trip_name;

    public TripModel() {
    }

    private String trip_date;
    private String trip_amount;
    private String trip_places;
    private String trip_desc;

    public String getTrip_places() {
        return trip_places;
    }

    public void setTrip_places(String trip_places) {
        this.trip_places = trip_places;
    }

    public String getTrip_desc() {
        return trip_desc;
    }

    public void setTrip_desc(String trip_desc) {
        this.trip_desc = trip_desc;
    }

    public TripModel(String trip_name,String trip_places, String trip_desc, String trip_date) {
        this.trip_name = trip_name;
        this.trip_date = trip_date;
        this.trip_places = trip_places;
        this.trip_desc = trip_desc;
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

    public String getTrip_amount() {
        return trip_amount;
    }

    public void setTrip_amount(String trip_amount) {
        this.trip_amount = trip_amount;
    }
}
