package app.aadil.travelindia;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO REMOVE THIS FILE

public class FeedData extends AppCompatActivity {


    InputStream in;
    BufferedReader reader;
    String line;

    String City;
    Map<String, String> Places;

    Integer Counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_data);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        try {
            in = this.getAssets().open("File.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            Places = new HashMap<String, String>();

            while ((line = reader.readLine()) != null) {
                if(Counter == 0) {
                    City = line;
                    Log.i("INFO ", City);
                }
                else {
                    Uri uri = Uri.parse(line);
                    String Name = uri.getQueryParameter("q");

                    if(Counter <= 9)
                        Places.put("Place0" + Counter, Name);
                    else
                        Places.put("Place" + Counter, Name);
                }

                ++Counter;
            }

            firebaseFirestore.collection("Places")
                    .document(City)
                    .set(Places)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}