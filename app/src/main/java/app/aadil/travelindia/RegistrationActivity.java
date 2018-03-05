package app.aadil.travelindia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView profilePictureIV;
    private EditText usernameET;
    private CountryCodePicker ccp;
    private EditText numberET;

    private int REQUEST_CODE = 777;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        profilePictureIV = (ImageView) findViewById(R.id.profilePic);
        usernameET = (EditText) findViewById(R.id.username);
        ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        numberET = (EditText) findViewById(R.id.number);
    }

    public void pickAndUploadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                profilePictureIV.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void Complete(View view) {
        String username = usernameET.getText().toString();
        String number = numberET.getText().toString();
        String country = ccp.getSelectedCountryName();

        if(username.length() == 0 || number.length() != 10) {
            if(username.length() == 0)
                Toast.makeText(getApplicationContext(), "Invalid Username.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Invalid Phone Number.", Toast.LENGTH_LONG).show();

            return;
        }

        final String[] url = {null};

        UploadImageService uploadImageService = new UploadImageService();
        uploadImageService.uploadImage(bitmap)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url[0] = taskSnapshot.getDownloadUrl().toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Registration Failed. " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
