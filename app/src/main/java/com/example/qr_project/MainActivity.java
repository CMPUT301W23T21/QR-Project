package com.example.qr_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/*
    FireStore is now connected to FireBase.
    FireStore here is to manage the data remotely in real time.
    FireStore can show what QR Codes have been added to the user's account,
    and it can also show what QR Codes have been removed from the collection.
 */

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;

}