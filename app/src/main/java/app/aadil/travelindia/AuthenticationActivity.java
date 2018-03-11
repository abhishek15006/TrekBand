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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AuthenticationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailET;
    private EditText passwordET;

    private Button loginB;
    private Button signupB;

    private GoogleSignInOptions gsio;
    private GoogleSignInClient gsic;

    private MaterialDialog dialog;

    private FirestoreService firestoreService;

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
        firestoreService = FirestoreService.getInstance();

        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);

        gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsic = GoogleSignIn.getClient(getApplicationContext(), gsio);
    }

    /*** VALIDATE EMAIL AND PASSWORD ***/
    public boolean validateEmailAndPassword(String email, String password) {
        if(!isValidEmail(email) || password.length() < 8) {
            if(!isValidEmail(email))
                Toasty.warning(getApplicationContext(), "Invalid Email Address.", Toast.LENGTH_LONG, true).show();
            else
                Toasty.warning(getApplicationContext(), "Password too short - minimum length is 8 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }
        return true;
    }

    public boolean isValidEmail(CharSequence text) {
        return (!TextUtils.isEmpty(text)) && Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    /*** EMAIL LOGIN ***/
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
                            firestoreService.getCurrentUser()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    Map<String, Object> user = document.getData();
                                                    FirestoreService.setUserDocumentID(document.getId());
                                                    if(Boolean.valueOf(user.get("registered").toString()))
                                                        gotoSearch(user.get("name").toString());
                                                    else
                                                        gotoRegistration("Successfully logged in.");
                                                }
                                            } else {
                                                createEmptyUserRecordAndGotoRegistration("Successfully logged in.");
                                            }
                                        }
                                    });
                        } else {
                            Toasty.error(getApplicationContext(), "Login with email failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG, true).show();
                        }

//                        dialog.dismiss();
                    }
                });

    }

    /*** CREATE NEW USER RECORD AND GOTO REGISTRATION ***/
    public void createEmptyUserRecordAndGotoRegistration(final String message) {
        UserModal user = new UserModal();

        firestoreService.storeUser(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        FirestoreService.setUserDocumentID(documentReference.getId());
                        gotoRegistration(message);
                    }
                });
    }

    /*** SHOW PROGRESS DIALOG ***/
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

    /*** EMAIL SIGNUP ***/
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
                        if (task.isSuccessful())
                            createEmptyUserRecordAndGotoRegistration("Welcome. You have successfully signed up.");
                        else
                            Toasty.error(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG, true).show();

//                        dialog.dismiss();
                    }
                });
    }

    /*** GOOGLE LOGIN ***/
    public void LoginWithGoogle(View view) {
        Intent googleLoginIntent = gsic.getSignInIntent();
        startActivityForResult(googleLoginIntent, 9001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, proceed to firebase authentication.
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showProgressDialog("Signing In", "Please Wait");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed.
                Toasty.error(getApplicationContext(), "Google Login failed. ", Toast.LENGTH_LONG, true).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firestoreService.getCurrentUser()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                                Map<String, Object> user = document.getData();
                                                FirestoreService.setUserDocumentID(document.getId());
                                                if(Boolean.valueOf(user.get("registered").toString()))
                                                    gotoSearch(user.get("name").toString());
                                                else
                                                    gotoRegistration("Successfully logged in with google.");
                                            }
                                        } else {
                                            createEmptyUserRecordAndGotoRegistration("Successfully logged in with google.");
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getApplicationContext(), "Google Login failed. " + e.getLocalizedMessage(), Toast.LENGTH_LONG, true).show();
                    }
                });
    }

    /*** REGISTRATION INTENT ***/
    public void gotoRegistration(String message) {
        Intent intent = new Intent(AuthenticationActivity.this, RegistrationActivity.class);
        Toasty.success(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
        startActivity(intent);
    }

    /*** SEARCH INTENT ***/
    public void gotoSearch(String name) {
        Intent intent = new Intent(AuthenticationActivity.this, SearchActivity.class);
        Toasty.success(getApplicationContext(), "Welcome back, " + name + "!", Toast.LENGTH_LONG, true).show();
        startActivity(intent);
    }
}
