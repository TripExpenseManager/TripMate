package android.app.models;

/**
 * Created by vinee_000 on 23-08-2017.
 */

public class TripImageModel {
    String tripName;
    boolean isLoaded = false;

    public TripImageModel() {
    }

    public TripImageModel(String tripName) {
        this.tripName = tripName;
    }

}
