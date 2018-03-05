package app.aadil.travelindia;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreService {

    private FirebaseFirestore firestore;

    public FirestoreService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> storeUser(UserModal user) {
        return firestore.collection("users").add(user);
    }
}
