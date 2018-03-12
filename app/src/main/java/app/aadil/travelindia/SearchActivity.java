package app.aadil.travelindia;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    RecyclerView placeDetails;
    protected GeoDataClient mGeoDataClient;
    private final String apiKey = "AIzaSyCu0itULZ1JMR0KkvYGmG-T4obUtq5eT8Y";
    public TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        test = (TextView)findViewById(R.id.jsonresponse);

        mGeoDataClient = Places.getGeoDataClient(this, null);

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("PLACES", "Place: " + place.getPlaceTypes().toString());
                getPlaces(place);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR", "An error occurred: " + status);
            }
        });
    }

    public void getPlaces(Place place){
        List<Integer> list = place.getPlaceTypes();
        boolean isPolitical = false;
        String url;

        for(Integer i : list){
            if(i==Place.TYPE_POLITICAL){
                isPolitical = true;
            }
        }

        if(isPolitical){
            url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=point+of+interest+in+" + place.getName().toString().replace(' ','+') + "&key=" + apiKey;
        }else{
            url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + place.getName().toString().replace(' ','+') + "&key=" + apiKey;
        }

        mapsRequest(url);

    }

    public void mapsRequest(String url){

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray results = response.getJSONArray("results");

                        }catch (JSONException e){
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        test.setText("Sorry");

                    }
                });

        queue.add(jsonObjectRequest);
    }

    private class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder>{
        List<PlaceModal> places;

        public class PlaceHolder extends RecyclerView.ViewHolder{
            public ImageView placeImageView;
            public TextView placeName;

            public PlaceHolder(View itemView){
                super(itemView);
                placeImageView = (ImageView)itemView.findViewById(R.id.place_image);
                placeName = (TextView)itemView.findViewById(R.id.place_name);
            }
        }

        public PlaceAdapter(List<PlaceModal> places){
            this.places = places;
        }

        @Override
        public PlaceAdapter.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType){
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.row_place, parent, false);

            PlaceHolder viewHolder = new PlaceHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(PlaceAdapter.PlaceHolder placeHolder, int position) {
            PlaceModal placeModal = places.get(position);
            ImageView placeImageView = placeHolder.placeImageView;
            TextView placeName = placeHolder.placeName;

            placeImageView.setImageBitmap(placeModal.getImage());
            placeName.setText(placeModal.getName());
        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return places.size();
        }
    }

}
