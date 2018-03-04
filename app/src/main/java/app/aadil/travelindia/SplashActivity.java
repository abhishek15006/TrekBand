package app.aadil.travelindia;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        }, SCREEN_TIMEOUT);
    }
}
