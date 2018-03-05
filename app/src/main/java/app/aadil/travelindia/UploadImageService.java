package app.aadil.travelindia;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class UploadImageService {

    private UploadTask uploadTask;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    public UploadImageService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://travel-india-c2621.appspot.com");
        imageRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
    }

    public UploadTask uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();
        uploadTask = imageRef .putBytes(data);
        return uploadTask;
    }
}