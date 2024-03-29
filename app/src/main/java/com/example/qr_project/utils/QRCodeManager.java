package com.example.qr_project.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qr_project.models.DatabaseResultCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class QRCodeManager {
    FirestoreDBHelper dbHelper = new FirestoreDBHelper();
    String hash;

    UserManager userManager = UserManager.getInstance();

    public QRCodeManager(String hash){
        this.hash = hash;
    }

    public void getGlobalRankingRealtime(DatabaseResultCallback<Integer> callback) {
        dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting documents", error);
                    callback.onFailure(error);
                    return;
                }

                if (querySnapshot != null) {
                    int rank = 1;
                    boolean found = false;
                    int highestScore = -1;

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) document.get("qrcodes");

                        if (qrCodesArray != null) {
                            for (Map<String, Object> code : qrCodesArray) {
                                int score = Math.toIntExact((Long) code.get("score"));

                                if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                    found = true;
                                    highestScore = score;
                                    break;
                                }
                            }
                        }

                        if (found) {
                            break;
                        }
                    }

                    if (found) {
                        // Calculate the rank based on the score
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) document.get("qrcodes");

                            if (qrCodesArray != null) {
                                for (Map<String, Object> code : qrCodesArray) {
                                    int score = Math.toIntExact((Long) code.get("score"));

                                    if (score > highestScore) {
                                        rank++;
                                    }
                                }
                            }
                        }
                        callback.onSuccess(rank);
                    } else {
                        callback.onFailure(new Exception("QR code not found"));
                    }
                } else {
                    callback.onFailure(new Exception("Error getting documents"));
                }
            }
        });
    }


    public void getFriendsQRCodeRankingRealtime(DatabaseResultCallback<Integer> callback) {
        dbHelper.setDocumentSnapshotListener("users", userManager.getUserID(), new EventListener<DocumentSnapshot>() {
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

                    AtomicInteger counter = new AtomicInteger(friendsList.size());
                    AtomicInteger rank = new AtomicInteger(1);
                    int[] highestScore = {-1};

                    for (String friendId : friendsList) {
                        dbHelper.setDocumentSnapshotListener("users", friendId, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot friendDocumentSnapshot, @Nullable FirebaseFirestoreException friendError) {
                                if (friendError != null) {
                                    Log.w(TAG, "Error getting document", friendError);
                                    callback.onFailure(friendError);
                                    return;
                                }

                                if (friendDocumentSnapshot != null && friendDocumentSnapshot.exists()) {
                                    List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) friendDocumentSnapshot.get("qrcodes");

                                    if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
                                        for (Map<String, Object> code : qrCodesArray) {
                                            int score = Math.toIntExact((Long) code.get("score"));

                                            if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                                highestScore[0] = score;
                                            }

                                            if (score > highestScore[0]) {
                                                rank.incrementAndGet();
                                            }
                                        }
                                    }

                                    if (counter.decrementAndGet() == 0) {
                                        if (highestScore[0] != -1) {
                                            callback.onSuccess(rank.get());
                                        } else {
                                            callback.onSuccess(0);
                                        }
                                    }
                                }
                            }
                        });
                    }
                } else {
                    callback.onFailure(new Exception("No document"));
                }
            }
        });
    }


    public void getUserQRCodeRankingRealtime(DatabaseResultCallback<Integer> callback) {
        dbHelper.setDocumentSnapshotListener("users", userManager.getUserID(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");

                    if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
                        int rank = 1;
                        boolean found = false;
                        int highestScore = -1;

                        for (Map<String, Object> code : qrCodesArray) {
                            int score = Math.toIntExact((Long) code.get("score"));

                            if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                found = true;
                                highestScore = score;
                                break;
                            }
                        }

                        if (found) {
                            // Calculate the rank based on the score
                            for (Map<String, Object> code : qrCodesArray) {
                                int score = Math.toIntExact((Long) code.get("score"));

                                if (score > highestScore) {
                                    rank++;
                                }
                            }
                            callback.onSuccess(rank);
                        } else {
                            callback.onSuccess(0);
                        }
                    } else {
                        callback.onFailure(new Exception("No QR codes in the array"));
                    }
                } else {
                    callback.onFailure(new Exception("No user"));
                }
            }
        });
    }


    public void getTotalScansRealtime(DatabaseResultCallback<Integer> callback) {
        dbHelper.setDocumentSnapshotListener("qrcodes", QRCodeManager.this.hash, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    ArrayList<String> users = (ArrayList<String>) documentSnapshot.get("users");

                    // Update the TextView for the number of times the QR code was scanned
                    callback.onSuccess(users.size());
                } else {
                    callback.onFailure(new Exception("QR Code doesn't exist"));
                }
            }
        });
    }


    public void addComment(Map<String, Object> comment) {

        Log.d(TAG, "Adding comment: " + comment);

        dbHelper.appendMapToArrayField("qrcodes", QRCodeManager.this.hash, "comments", comment,
            new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "Comment added successfully");
                }
            },
            new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error adding comment: " + e.getMessage());
                }
            }
        );
    }

    public void removeComment(Map<String, Object> comment){
        dbHelper.removeMapFromArrayField("qrcodes", QRCodeManager.this.hash, "comments", comment, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Comment successfully removed from array field!");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error removing comment from array field", e);
            }
        });
    }

    public void getAllScannersRealtime(DatabaseResultCallback<List<String>> callback) {
        dbHelper.setDocumentSnapshotListener("qrcodes", QRCodeManager.this.hash, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(new Exception("QR Code doesn't exist"));
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<String> users = (List<String>) documentSnapshot.get("users");
                    callback.onSuccess(users);
                } else {
                    callback.onFailure(new Exception("QR Code doesn't exist"));
                }
            }
        });
    }


    public void getAllUsersRealtime(DatabaseResultCallback<List<Friend>> callback) {
        List<Friend> friends = Collections.synchronizedList(new ArrayList<>());
        getAllScannersRealtime(new DatabaseResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                if (result == null || result.isEmpty()) {
                    callback.onSuccess(friends);
                    return;
                }

                AtomicInteger completedOperations = new AtomicInteger(0);
                for (String id : result) {
                    dbHelper.setDocumentSnapshotListener("users", id, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Error getting document", error);
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                String username = (String) documentSnapshot.get("username");
                                int totalScore = Math.toIntExact((Long) documentSnapshot.get("totalScore"));
                                Friend friend = new Friend(username, totalScore, id);
                                friends.add(friend);
                            }

                            // Check if all operations are completed
                            if (completedOperations.incrementAndGet() == result.size()) {
                                callback.onSuccess(friends);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error getting people: ", e);
                callback.onFailure(e);
            }
        });
    }


    public void getAllCommentsRealtime(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        dbHelper.setDocumentSnapshotListener("qrcodes", QRCodeManager.this.hash, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<Map<String, Object>> comments = (List<Map<String, Object>>) documentSnapshot.get("comments");

                    // Update the TextView for the number of times the QR code was scanned
                    callback.onSuccess(comments);
                } else {
                    callback.onFailure(new Exception("QR Code doesn't exist"));
                }
            }
        });
    }


    public void hasUserScannedRealtime(DatabaseResultCallback<Boolean> callback) {
        dbHelper.setDocumentSnapshotListener("users", userManager.getUserID(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
                    boolean qrCodeFound = false;

                    if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
                        for (Map<String, Object> code : qrCodesArray) {
                            if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                qrCodeFound = true;
                                break;
                            }
                        }
                    }
                    callback.onSuccess(qrCodeFound);
                } else {
                    callback.onFailure(new Exception("No document"));
                }
            }
        });
    }


    public void getQRCodeRealtime(DatabaseResultCallback<QR_Code> callback) {
        AtomicBoolean qrCodeFound = new AtomicBoolean(false);

        dbHelper.setDocumentSnapshotListener("users", userManager.getUserID(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    qrCodeFound.set(processDocument(documentSnapshot, callback, true));
                }
            }
        });

        dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting documents", error);
                    callback.onFailure(error);
                    return;
                }

                if (!qrCodeFound.get()) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (!document.getId().equals(userManager.getUserID())) {
                            boolean qrCodeFoundInOtherDocs = processDocument(document, callback, false);
                            if (qrCodeFoundInOtherDocs) {
                                qrCodeFound.set(true);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }



    private boolean processDocument(DocumentSnapshot documentSnapshot, DatabaseResultCallback<QR_Code> callback, boolean foundWithUser) {
        List<Map<String, Object>> qrCodesArray = extractQRCodeArray(documentSnapshot);

        if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
            Map<String, Object> selectedQRCode = findQRCodeByHash(qrCodesArray, QRCodeManager.this.hash);

            if (selectedQRCode != null) {
                QR_Code qrCode = createQRCodeObject(selectedQRCode, foundWithUser);
                callback.onSuccess(qrCode);
                return true;
            }
        }
        return false;
    }

    private List<Map<String, Object>> extractQRCodeArray(DocumentSnapshot documentSnapshot) {
        return (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
    }

    private Map<String, Object> findQRCodeByHash(List<Map<String, Object>> qrCodesArray, String hash) {
        for (Map<String, Object> code : qrCodesArray) {
            if (String.valueOf(code.get("hash")).equals(hash)) {
                return code;
            }
        }
        return null;
    }

    private QR_Code createQRCodeObject(Map<String, Object> selectedQRCode, boolean foundWithUser) {
        int score = Math.toIntExact((Long) selectedQRCode.get("score"));
        String name = (String) selectedQRCode.get("name");
        String face = (String) selectedQRCode.get("face");
        String photoUrl = (String) selectedQRCode.get("photo");
        GeoPoint location = (GeoPoint) selectedQRCode.get("location");
        String hash = (String) selectedQRCode.get("hash");

        String photo;
        if (foundWithUser) {
            photo = photoUrl;
        } else {
            photo = null;
        }

        return new QR_Code(score, name, face, photo, location, hash, foundWithUser);
    }


}
