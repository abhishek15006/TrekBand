//package app.aadil.travelindia;
//
//import com.google.firebase.FirebaseOptions;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.firestore.Firestore;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.firebase_firestore.*;
//import com.google.firebase.firestore.*;
//
//
//public class FirestoreService {
//
//    private FirestoreService() {
//        FirebaseFirestore.getInstance();
//    }
//
//    public FirestoreService getInstance() {
//
//    }
//}

//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//                Map<String, String> data = new HashMap<>();
//        data.put("test", "Hello World");
//
//        firestore.collection("test").add(data)
//        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//@Override
//public void onSuccess(DocumentReference documentReference) {
//        Log.d("DEBUG", "DocumentSnapshot successfully written!");
//        }
//        })
//        .addOnFailureListener(new OnFailureListener() {
//@Override
//public void onFailure(@NonNull Exception e) {
//        Log.w("DEBUG", "Error writing document", e);
//        }
//        });