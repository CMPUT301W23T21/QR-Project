package com.example.qr_project.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class QRCodeActivity extends AppCompatActivity {
    /**
     * This allows to fetch the id of the "activity_qrcode" button
     * After that, it will move on the next step/page
     * @param savedInstanceState   a package to be called to execute the QRCodeActivity
     */

    private FirebaseFirestore db;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
    }

    /**
     * For the use and the feature of Delete click button
     *
     * @param view The text view which is pressed
     */
    public void onDeleteClick(View view) {
        Toast.makeText(this, "Delete Button Click", Toast.LENGTH_SHORT).show();
        String userID = "dfgg";
        collectionReference
                .document(userID)
                        .get("qrcodes")
        cityList.setItemChecked(-1, true);
        selectedIndex = -1;
    }
}