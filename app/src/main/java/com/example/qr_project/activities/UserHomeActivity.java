package com.example.qr_project.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.example.qr_project.utils.QR_Code;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class UserHomeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String userId;
    List<Map<String, Object>> qrCodes;

    TextView totalScore;

    TextView globalRank;
    TextView friendRank;
    TextView totalQrCodes;

    TextView qrCode1Name;
    TextView qrCode1Score;
    TextView qrCode2Name;
    TextView qrCode2Score;
    TextView qrCode3Name;
    TextView qrCode3Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userId = getIntent().getStringExtra("userId");
        db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("users");
        DocumentReference docRef = collRef.document(userId);

        totalScore = findViewById(R.id.user_total_score);
        globalRank = findViewById(R.id.global_rank);
        friendRank = findViewById(R.id.friend_rank);
        totalQrCodes = findViewById(R.id.total_qr_codes);
        qrCode1Name = findViewById(R.id.qr_code_name_1);
        qrCode2Name = findViewById(R.id.qr_code_name_2);
        qrCode3Name = findViewById(R.id.qr_code_name_3);
        qrCode1Score = findViewById(R.id.qr_code_score_1);
        qrCode2Score = findViewById(R.id.qr_code_score_2);
        qrCode3Score = findViewById(R.id.qr_code_score_3);

        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<Map<String, Integer>> rankings = new ArrayList<>();
                for (QueryDocumentSnapshot doc: value) {
                    Map<String, Integer> userId_score_pair = new HashMap<>();
                    userId_score_pair.put(doc.getId().toString(), Math.toIntExact((Long) doc.get("totalScore")));
                    rankings.add(userId_score_pair);
                }
                Collections.sort(rankings, (x, y) -> x.entrySet().iterator().next().getValue().compareTo(y.entrySet().iterator().next().getValue()));
                String rank;
                int counter = rankings.size();
                for (Map<String, Integer> x: rankings) {
                    if (x.containsKey(userId)) {
                        rank = "Global Rank: " + String.valueOf(counter);
                        globalRank.setText(rank);
                        break;
                    }
                    counter--;
                }

            }
        });

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                totalScore.setText(value.get("totalScore").toString());

                qrCodes = (List<Map<String, Object>>) value.get("qrcodes");
                ArrayList<Integer> scores = new ArrayList<>();
                for (Map<String, Object> qrCode: qrCodes) {
                    scores.add(Math.toIntExact((Long) qrCode.get("score")));
                }

                int maxScore = 0;
                int idx = 0;
                try {
                    maxScore = Collections.max(scores);
                    idx = scores.indexOf(maxScore);
                    qrCode1Score.setText(String.valueOf(maxScore));
                    qrCode1Name.setText((String) qrCodes.get(idx).get("name"));
                    scores.remove(idx);

                    maxScore = Collections.max(scores);
                    idx = scores.indexOf(maxScore);
                    qrCode2Score.setText(String.valueOf(maxScore));
                    qrCode2Name.setText((String) qrCodes.get(idx).get("name"));
                    scores.remove(idx);

                    maxScore = Collections.max(scores);
                    idx = scores.indexOf(maxScore);
                    qrCode3Score.setText(String.valueOf(maxScore));
                    qrCode3Name.setText((String) qrCodes.get(idx).get("name"));
                } catch (NoSuchElementException e) {

                }

                String totalQr;
                totalQr = "Total QR Codes: " + String.valueOf(scores.size());
                totalQrCodes.setText(totalQr);
            }
        });

    }

    /**
     * When the user clicks the camera button, this method will be called
     * and will open the camera to scan a QR or barcode
     *
     * @param view The text view which is pressed
     */
    public void onCameraClick(View view) {
        //Toast.makeText(this, "Camera Button Click", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }


    /**
     * For the use and the feature of the map button
     *
     * @param view The text view which is pressed
     */
    public void onMapClick(View view) {
        Toast.makeText(this, "Map Button Click", Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts the LeaderboardActivity
     * @param view The text view which is pressed
     */
    public void onLeaderboardClick(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void onViewAllClick(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("filter", "user");
        startActivity(intent);
    }

    public void onViewUserProfile(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

}