package com.example.qr_project.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.qr_project.models.DatabaseResultCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LeaderboardManager {

    FirestoreDBHelper dbHelper = new FirestoreDBHelper();
    String hash;
    UserManager userManager = UserManager.getInstance();
    String userID = userManager.getUserID();

    public LeaderboardManager(){

    }

    public void getRealtimeTopFriendsQRCodes(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        String userId = userManager.getUserID();

        dbHelper.setDocumentSnapshotListener("users", userId, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<Map<String, Object>> friendsData = (List<Map<String, Object>>) documentSnapshot.get("friends");
                    List<String> friendsList = new ArrayList<>();
                    for (Map<String, Object> friendData : friendsData) {
                        friendsList.add((String) friendData.get("userID"));
                    }
                    friendsList.add(userManager.getUserID()); // Add the user to the list for comparison

                    dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Error getting documents", error);
                                callback.onFailure(error);
                                return;
                            }

                            List<Map<String, Object>> allQRCodes = new ArrayList<Map<String, Object>>();
                            for (QueryDocumentSnapshot userDocument : querySnapshot) {
                                if (friendsList.contains(userDocument.getId())) {
                                    List<Map<String, Object>> qrcodes = (List<Map<String, Object>>) userDocument.get("qrcodes");
                                    if (qrcodes != null && !qrcodes.isEmpty()){
                                        for (Map<String, Object> qrCode : qrcodes){
                                            allQRCodes.add(qrCode);
                                        }
                                    }
                                }
                            }
                            // Sort final list
                            allQRCodes.sort((a,b) -> ( (Long) b.get("score")).compareTo(( (Long) a.get("score")) ));
                            callback.onSuccess(allQRCodes);
                        }
                    });
                } else {
                    callback.onFailure(new Exception("User document does not exist"));
                }
            }
        });
    }


    public void getRealtimeTopFriendsTotalScores(DatabaseResultCallback<List<Friend>> callback) {
        String userID = userManager.getUserID();

        dbHelper.setDocumentSnapshotListener("users", userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<Map<String, Object>> friendsData = (List<Map<String, Object>>) documentSnapshot.get("friends");
                    List<String> friendsList = new ArrayList<>();
                    for (Map<String, Object> friendData : friendsData) {
                        friendsList.add((String) friendData.get("userID"));
                    }
                    friendsList.add(userManager.getUserID()); // Add the user to the list for comparison

                    dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Error getting documents", error);
                                callback.onFailure(error);
                                return;
                            }

                            List<Friend> friends = new ArrayList<>();
                            for (DocumentSnapshot userDocument : querySnapshot.getDocuments()) {
                                if (friendsList.contains(userDocument.getId())) {
                                    String name = (String) userDocument.get("username");
                                    String id = userDocument.getId();
                                    int score = Math.toIntExact((Long) userDocument.get("totalScore"));
                                    Friend friend = new Friend(name, score, id);
                                    friends.add(friend);
                                }
                            }
                            // Sort friends by score in descending order
                            Collections.sort(friends, new Comparator<Friend>() {
                                @Override
                                public int compare(Friend friend1, Friend friend2) {
                                    return Integer.compare(friend2.getScore(), friend1.getScore());
                                }
                            });

                            callback.onSuccess(friends);
                        }
                    });
                } else {
                    callback.onFailure(new Exception("User document does not exist"));
                }
            }
        });
    }


    public void getRealtimeTopGlobalQRCodes(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(error);
                }

                if (value != null && !value.isEmpty()) {
                    List<Map<String, Object>> topQRCodes = new ArrayList<>();
                    // add qr codes from each user to topQRCodes
                    for (QueryDocumentSnapshot doc : value) {
                        List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) doc.get("qrcodes");
                        if (qrCodes != null && !qrCodes.isEmpty()) {
                            for (Map<String, Object> qrCode : qrCodes) {
                                topQRCodes.add(qrCode);
                            }
                        }
                    }
                    // Sort final list
                    topQRCodes.sort((a,b) -> ( (Long) b.get("score")).compareTo(( (Long) a.get("score")) ));
                    callback.onSuccess(topQRCodes);
                } else {
                    callback.onFailure(new Exception("Error with collection"));
                }
            }
        });
    }


    public void getRealtimeTopUserQRCodes(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        if (this.userID == null) {
            callback.onFailure(new Exception("User ID is null"));
            return;
        }

        dbHelper.setDocumentSnapshotListener("users", this.userID,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(error);
                }

                if (value != null && value.exists()) {
                    Map<String, Object> data = value.getData();
                    if (data != null){
                        List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) data.get("qrcodes");
                        if (qrCodes != null) {
                            // Sort QR codes based on score
                            qrCodes.sort((a,b) -> ( (Long) b.get("score")).compareTo(( (Long) a.get("score")) ));

                            callback.onSuccess(qrCodes);
                        }
                    } else {
                        callback.onFailure(new Exception("No data in given document"));
                    }

                } else {
                    callback.onFailure(new Exception("Document does not exist"));
                }
            }
        });
    }


    public void getRealtimeGlobalTotalScores(DatabaseResultCallback<List<Friend>> callback) {
        dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(error);
                }

                if (value != null && !value.isEmpty()) {
                    List<Friend> topScores = new ArrayList<>();
                    // add qr codes from each user to topQRCodes
                    for (QueryDocumentSnapshot doc : value) {
                        String name = (String) doc.get("username");
                        String id = (String) doc.get("userID");
                        int score = Math.toIntExact((Long) doc.get("totalScore"));

                        Friend friend = new Friend(name, score, id);
                        topScores.add(friend);

                    }

                    // Sort final list
                    topScores.sort((a,b) -> ( Long.valueOf(b.getScore())).compareTo(Long.valueOf( a.getScore()) ));
                    callback.onSuccess(topScores);

                } else {
                    callback.onFailure(new Exception("Error with collection"));
                }
            }
        });
    }

}
