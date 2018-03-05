package app.aadil.travelindia;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private String[] quotes;
    private TextView quoteTV;
    private Random random;
     private static int SCREEN_TIMEOUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        random = new Random();
        quoteTV = (TextView) findViewById(R.id.quoteTV);

        quotes = getResources().getStringArray(R.array.quotes);

        int index = random.nextInt(quotes.length);

        quoteTV.setText(String.format("\"%s\"", quotes[index]));

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
                    Intent searchIntent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                    startActivity(searchIntent);
                }

                finish();
            }
        }, SCREEN_TIMEOUT);
    }
}
