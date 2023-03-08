package com.example.qr_project;

/*
   FireStore can store the QR Codes in the collection online and remotely.
   Getting the data from the user and then uploading the date online right away to the collection
   Every QR Code will be stored online on Cloud FireBase
 */


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireStore<FirebaseDatabase, DatabaseReference> {

    private static FireStore getInstance() {
        return null;
    }
/*
    // Access a Cloud Firestore instance from your Activity
    FireStore db = FireStore.getInstance();

    FirebaseFirestore firestore;

    // Write a message to database (this is a test for the firebase firestore)
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    myRef.setValue("Hello, FireStore FireBase is working now");

 */


}
