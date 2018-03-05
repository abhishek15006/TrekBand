package app.aadil.travelindia;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailET;
    private EditText passwordET;

    private Button loginB;
    private Button signupB;

    private GoogleSignInOptions gsio;
    private GoogleSignInClient gsic;

    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Integer resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, resultCode, 0);
            if (dialog != null) {
                dialog.show();
            }
        }

        mAuth = FirebaseAuth.getInstance();

        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);

        gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsic = GoogleSignIn.getClient(getApplicationContext(), gsio);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        System.out.println("DEBUG" + currentUser);

        if(currentUser != null) {
            Intent searchActivity = new Intent(AuthenticationActivity.this, SearchActivity.class);
            startActivity(searchActivity);
        }
    }

    public boolean validateEmailAndPassword(String email, String password) {
        if(!isValidEmail(email) || password.length() < 8) {
            if(!isValidEmail(email))
                Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Password too short - minimum length is 8 characters", Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    public void Login(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        // Input Validation
        if(!validateEmailAndPassword(email, password))
            return;

        showProgressDialog("Signing In", "Please Wait");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Log In success.", Toast.LENGTH_SHORT).show();
                            Intent searchIntent = new Intent(AuthenticationActivity.this, SearchActivity.class);
                            startActivity(searchIntent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Log In failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                });

    }

    public void showProgressDialog(String title, String content) {
        dialog = new MaterialDialog.Builder(this)
                .contentColor(getResources().getColor(R.color.grey))
                .backgroundColor(getResources().getColor(R.color.white))
                .titleColor(getResources().getColor(R.color.grey))
                .widgetColorRes(R.color.grey)
                .backgroundColorRes(R.color.white)
                .title(title)
                .content(content)
                .progress(true, 0)
                .build();

        dialog.show();
    }

    public void Signup(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        // Input Validation
        if(!validateEmailAndPassword(email, password))
            return;

        showProgressDialog("Signing Up", "Please Wait");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Sign Up success.", Toast.LENGTH_SHORT).show();

                            Intent registrationIntent = new Intent(AuthenticationActivity.this, RegistrationActivity.class);
                            startActivity(registrationIntent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Signup failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                });
    }

    public boolean isValidEmail(CharSequence text) {
        return (!TextUtils.isEmpty(text)) && Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    public void LoginWithGoogle(View view) {
        Intent googleLoginIntent = gsic.getSignInIntent();
        startActivityForResult(googleLoginIntent, 9001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        showProgressDialog("Signing In", "Please Wait");

        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, proceed to firebase authentication
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(getApplicationContext(), "Google sign in failed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Firebase sign in success
                            Intent registerIntent = new Intent(AuthenticationActivity.this, RegistrationActivity.class);
                            startActivity(registerIntent);
                        } else {
                            // Firebase sign in failed with given google credentials
                            Toast.makeText(getApplicationContext(), "Login with google failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });
    }

    public void LoginWithFacebook(View view) {
    }
}
