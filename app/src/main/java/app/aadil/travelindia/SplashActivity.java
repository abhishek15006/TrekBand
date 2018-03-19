package app.aadil.travelindia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class SplashActivity extends AppCompatActivity {

    private String[] quotes;
    private TextView quoteTV;
    private Random random;
    private static int SCREEN_TIMEOUT = 2500;
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView back=(ImageView)findViewById(R.id.imageView);
        ImageView needle=(ImageView)findViewById(R.id.imageView2);

        needle.animate().rotationBy(3600).setDuration(10000);


        random = new Random();
        quoteTV = (TextView) findViewById(R.id.quoteTV);

        quotes = getResources().getStringArray(R.array.quotes);

        int index = random.nextInt(quotes.length);

        quoteTV.setText(String.format("\"%s\"", quotes[index]));

        firestoreService = FirestoreService.getInstance();

        configToasty();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Compile database of 1554 locations

        final SearchData searchData = SearchData.getInstance();
        searchData.getPlacesFromFirestore()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        int size = documentSnapshots.size();
                        List<DocumentSnapshot> documents = documentSnapshots.getDocuments();

                        for(int i = 0; i < size; ++i) {
                            DocumentSnapshot documentSnapshot = documents.get(i);
                            String City = documentSnapshot.getId();
                            List<String> Places = new ArrayList<>();

                            Map<String, Object> pairs = documentSnapshot.getData();
                            for(int j = 0; j < pairs.size(); ++j) {
                                int index = j + 1;
                                if(index <= 9)
                                    Places.add(String.valueOf(pairs.get("Place0" + index)));
                                else
                                    Places.add(String.valueOf(pairs.get("Place" + index)));
                            }

                            SearchData.addCity(City, Places);
                        }

                        startProcedures();
//                        List<String> AllPlaces = SearchData.getAllPlaces();
//                        Log.i("DEBUG ", String.valueOf(AllPlaces.size()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void startProcedures() {
        new Handler().postDelayed(new Runnable() {

            private FirebaseAuth mAuth;

            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                if(user == null) {
                    Intent authIntent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                    startActivity(authIntent);
                }
                else {
                    // Get current user document in 'users' collection
                    firestoreService.getCurrentUser()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    // If successful, save the document id.
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> user = document.getData();
                                            FirestoreService.setUserDocumentID(document.getId());

                                            // Now, Proceed based on current user state.
                                            if(Boolean.valueOf(user.get("registered").toString()))
                                                gotoSearch(user.get("name").toString());
                                            else
                                                gotoRegistration("Welcome Back!!!");
                                        }
                                    }
                                }
                            });
                }
            }
        }, SCREEN_TIMEOUT);
    }

    public void configToasty() {
        Toasty.Config.getInstance()
                .setTextSize(18)
                .apply();
    }

    /*** REGISTRATION INTENT ***/
    public void gotoRegistration(String message) {
        Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
        Toasty.success(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
        startActivity(intent);
    }

    /*** SEARCH INTENT ***/
    public void gotoSearch(String name) {
        Intent intent = new Intent(SplashActivity.this, SearchActivity.class);
        Toasty.success(getApplicationContext(), "Welcome back, " + name + "!", Toast.LENGTH_LONG, true).show();
        startActivity(intent);
    }
}
