package app.aadil.travelindia;

import java.util.ArrayList;

/**
 * Created by abhishek on 11/3/18.
 */

public class PlaceInfo {
    private ArrayList<PlaceModal> places;

    public PlaceInfo(){
        places = new ArrayList<>();
    }

    public void addPlaces(PlaceModal placeModal){
        places.add(placeModal);
    }

    public ArrayList<PlaceModal> getList(){
        return places;
    }
}
