package app.aadil.travelindia;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreService {

    private static FirestoreService firestoreService = null;
    private static FirebaseFirestore firebaseFirestore = null;
    private static String userDocumentID = null;

    private FirestoreService() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public static FirestoreService getInstance() {
        if(firestoreService == null)
            firestoreService = new FirestoreService();
        return firestoreService;
    }

    public Task<DocumentReference> storeUser(UserModal user) {
        return firebaseFirestore.collection("users").add(user);
    }

    public Task<QuerySnapshot> getCurrentUser() {
        CollectionReference users = firebaseFirestore.collection("users");
        Query query = users.whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        return query.get();
    }

    public static void reset() {
        firestoreService = null;
        firebaseFirestore = null;
        userDocumentID = null;
    }

    public static void setUserDocumentID(String id) {
        userDocumentID = id;
    }

    public static String getUserDocumentID() {
        return userDocumentID;
    }

    public Task<QuerySnapshot> getPlaces() {
        return firebaseFirestore.collection("Places").get();
    }

    public Task<DocumentSnapshot> getCityPlaces(String City) {
        return firebaseFirestore.collection("Places").document(City).get();
    }
}
