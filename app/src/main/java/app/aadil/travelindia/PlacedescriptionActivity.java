package app.aadil.travelindia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlacedescriptionActivity extends AppCompatActivity {
    public static String PLACE_NAME_KEY = "place_name_key";
    public static String PLACE_IMAGE_KEY = "place_image_key";
    public static String PLACE_ID_KEY = "place_id_key";

    private TextView placeName,placeDescription;
    private ImageView placeImage;
    private String placeId;
    private List<ImageView> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedescription);

        placeImage = (ImageView)findViewById(R.id.pd_image);
        placeName = (TextView)findViewById(R.id.pd_name);
        placeDescription = (TextView)findViewById(R.id.pd_description);

        placeName.setText(getIntent().getStringExtra(PLACE_NAME_KEY));
        placeImage.setImageBitmap((Bitmap)getIntent().getParcelableExtra(PLACE_IMAGE_KEY));
        placeId = getIntent().getStringExtra(PLACE_ID_KEY);

        images = new ArrayList<ImageView>();

        descriptionRequest(placeName.getText().toString());
    }

    public void descriptionRequest(String name){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + name.replaceAll(" ","%20");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONObject pages = response.getJSONObject("query").getJSONObject("pages");

                            String extract = "";

                            for(Iterator key = pages.keys(); key.hasNext();) {
                                JSONObject temp = pages.getJSONObject(key.next().toString());
                                Log.i("KEY_DATA",key.toString());
                                extract = temp.getString("extract");

                            }

                            placeDescription.setText(extract);

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

//        String url_2 = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + SearchActivity.apiKey;;
//
//        JsonObjectRequest jsonObjectRequest_2 = new JsonObjectRequest
//                (Request.Method.GET, url_2, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try{
//
//                            JSONArray photos = response.getJSONObject("result").getJSONArray("photos");
//
//                            for(int i=0;i<photos.length();++i){
//
//                            }
//
//                        }catch (JSONException e){
//                            Toast.makeText(getApplicationContext(),
//                                    "Json parsing error 2: " + e.getMessage(),
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(),
//                                "Photos search error" ,
//                                Toast.LENGTH_LONG)
//                                .show();
//
//                    }
//                });
//
//        queue.add(jsonObjectRequest_2);
    }


}
