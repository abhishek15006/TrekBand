package app.aadil.travelindia;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.*;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
    private final String apiKey = "AIzaSyCu0itULZ1JMR0KkvYGmG-T4obUtq5eT8Y";

    RecyclerView recyclerView;
    PlaceAdapter placeAdapter;

    protected GeoDataClient mGeoDataClient;

    List<PlaceModal> placeModalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        placeModalList = new ArrayList<>();
        placeAdapter = new PlaceAdapter(placeModalList);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(placeAdapter);

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
                            placeModalList.clear();
                            for(int i=0;i<results.length();++i){
                                JSONObject placeJSONInfo = results.getJSONObject(i);
                                placeModalList.add(new PlaceModal(getApplicationContext(), placeAdapter, placeJSONInfo.getString("name"),null));

                                placeAdapter.notifyDataSetChanged();

                                String photoRef = placeJSONInfo.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                                String photoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&maxheight=300&photoreference=" + photoRef +"&key=" + apiKey;

                                Picasso.get()
                                   .load(photoURL)
                                   .into(placeModalList.get(i));
                            }



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
                        Toast.makeText(getApplicationContext(),
                                "Search error" ,
                                Toast.LENGTH_LONG)
                                .show();

                    }
                });

        queue.add(jsonObjectRequest);
    }

    public class PlaceHolder extends RecyclerView.ViewHolder{
        public ImageView placeImageView;
        public TextView placeName;

        public PlaceHolder(View itemView){
            super(itemView);
            placeImageView = (ImageView)itemView.findViewById(R.id.place_image);
            placeName = (TextView)itemView.findViewById(R.id.place_name);
        }
    }

    private class PlaceAdapter extends RecyclerView.Adapter<PlaceHolder>{
        List<PlaceModal> places;

        public PlaceAdapter(List<PlaceModal> places){
            this.places = places;
        }

        @Override
        public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType){
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.place_card, parent, false);

            PlaceHolder viewHolder = new PlaceHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(PlaceHolder placeHolder, int position) {
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
