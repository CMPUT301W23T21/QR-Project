package com.example.qr_project.utils;

import androidx.annotation.NonNull;

import com.example.qr_project.models.DatabaseResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

    public LeaderboardManager(){

    }


    public void getTopGlobalQRCodes(DatabaseResultCallback<List<QR_Code>> callback) {
        String userId = userManager.getUserID();

        dbHelper.getAllDocuments("users", new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<Map<String, Object>> qrCodeMaps = (List<Map<String, Object>>) document.get("qrcodes");
                        List<QR_Code> qrCodes = new ArrayList<>();
                        if (qrCodeMaps != null) {
                            for (Map<String, Object> qrCodeMap : qrCodeMaps) {
                                String name = (String) qrCodeMap.get("name");
                                int score = (Math.toIntExact((Long) qrCodeMap.get("score")));
                                String hash = (String) qrCodeMap.get("hash");
                                String face = (String) qrCodeMap.get("face");
                                QR_Code qrCode = new QR_Code(score, name, face, hash);
                                qrCodes.add(qrCode);
                            }
                            // Sort the QR codes by score in descending order
                            Collections.sort(qrCodes, new Comparator<QR_Code>() {
                                @Override
                                public int compare(QR_Code qrCode1, QR_Code qrCode2) {
                                    return Integer.compare(qrCode2.getScore(), qrCode1.getScore());
                                }
                            });
                            callback.onSuccess(qrCodes);
                            return;
                        } else {
                            callback.onFailure(new Exception("QR codes array not found in user document"));
                            return;
                        }
                    }
                    callback.onFailure(new Exception("User document not found"));
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public void getTopUserQRCodes(DatabaseResultCallback<List<QR_Code>> callback){
        String userId = userManager.getUserID();

        dbHelper.getDocument("users", userId, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> qrcodes = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
                    List<QR_Code> topUserCodes = new ArrayList<>();
                    for (Map<String, Object> code : qrcodes){
                        String name = (String) code.get("name");
                        int score = (Math.toIntExact((Long) code.get("score")));
                        String hash = (String) code.get("hash");
                        String face = (String) code.get("face");
                        QR_Code qrCode = new QR_Code(score, name, face, hash);
                        topUserCodes.add(qrCode);
                    }
                    // Sort the QR codes by score in descending order
                    Collections.sort(topUserCodes, new Comparator<QR_Code>() {
                        @Override
                        public int compare(QR_Code qrCode1, QR_Code qrCode2) {
                            return Integer.compare(qrCode2.getScore(), qrCode1.getScore());
                        }
                    });
                    callback.onSuccess(topUserCodes);
                    return;
                } else {
                    callback.onFailure(new Exception("QR codes array not found in user document"));
                    return;
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getTopFriendsQRCodes(DatabaseResultCallback<List<QR_Code>> callback) {
        String userId = userManager.getUserID();

        dbHelper.getDocument("users", userId, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> friendsData = (List<Map<String, Object>>) documentSnapshot.get("friends");
                    List<String> friendsList = new ArrayList<>();
                    for (Map<String, Object> friendData : friendsData) {
                        friendsList.add((String) friendData.get("userID"));
                    }
                    friendsList.add(userManager.getUserID()); // Add the user to the list for comparison


                    dbHelper.getAllDocuments("users", new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<QR_Code> allQRCodes = new ArrayList<>();
                                for (DocumentSnapshot userDocument : task.getResult()) {
                                    if (friendsList.contains(userDocument.getId())) {
                                        List<Map<String, Object>> qrcodes = (List<Map<String, Object>>) userDocument.get("qrcodes");
                                        for (Map<String, Object> code : qrcodes) {
                                            String name = (String) code.get("name");
                                            int score = Math.toIntExact((Long) code.get("score"));
                                            String hash = (String) code.get("hash");
                                            String face = (String) code.get("face");
                                            QR_Code qrCode = new QR_Code(score, name, face, hash);
                                            allQRCodes.add(qrCode);
                                        }
                                    }
                                }

                                // Sort the QR codes by score in descending order
                                Collections.sort(allQRCodes, new Comparator<QR_Code>() {
                                    @Override
                                    public int compare(QR_Code qrCode1, QR_Code qrCode2) {
                                        return Integer.compare(qrCode2.getScore(), qrCode1.getScore());
                                    }
                                });

                                callback.onSuccess(allQRCodes);
                            } else {
                                callback.onFailure(new Exception("Error getting documents: " + task.getException()));
                            }
                        }
                    });
                } else {
                    callback.onFailure(new Exception("User document does not exist"));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getTopFriendsTotalScores(DatabaseResultCallback<List<Friend>> callback){
        String userID = userManager.getUserID();

        dbHelper.getDocument("users", userID, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> friendsData = (List<Map<String, Object>>) documentSnapshot.get("friends");
                    List<String> friendsList = new ArrayList<>();
                    for (Map<String, Object> friendData : friendsData) {
                        friendsList.add((String) friendData.get("userID"));
                    }
                    friendsList.add(userManager.getUserID()); // Add the user to the list for comparison

                    dbHelper.getAllDocumentsOrdered("users", "totalScore", false, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Friend> friends = new ArrayList<>();
                                for (DocumentSnapshot userDocument : task.getResult()) {
                                    if (friendsList.contains(userDocument.get("userID"))) {
                                        String name = (String) userDocument.get("username");
                                        String id = (String) userDocument.get("userID");
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


                            } else {
                                callback.onFailure(new Exception("Error getting documents: " + task.getException()));
                            }
                        }
                    });
                } else {
                    callback.onFailure(new Exception("User document does not exist"));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }


    public void getTopGlobalTotalScores(DatabaseResultCallback<List<Friend>> callback){
        dbHelper.getAllDocumentsOrdered("users", "totalScore", false, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Friend> globalList = new ArrayList<>();
                    for (DocumentSnapshot document: task.getResult()){
                        String name = (String) document.get("username");
                        String id = (String) document.get("userID");
                        int score = Math.toIntExact((Long) document.get("totalScore"));

                        Friend friend = new Friend(name, score, id);
                        globalList.add(friend);
                    }

                    // Sort the QR codes by score in descending order
                    Collections.sort(globalList, new Comparator<Friend>() {
                        @Override
                        public int compare(Friend person1, Friend person2) {
                            return Integer.compare(person1.getScore(), person2.getScore());
                        }
                    });

                    callback.onSuccess(globalList);
                } else {
                    callback.onFailure(new Exception("Error getting documents: " + task.getException()));
                }
            }
        });
    }
}