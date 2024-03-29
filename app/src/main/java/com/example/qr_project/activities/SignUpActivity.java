package com.example.qr_project.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.example.qr_project.utils.Player;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    Button signUpButton;

    FirebaseFirestore db;

    // SignUpActivity when doing testing
    public SignUpActivity(Comparable<String> user_name, String email, String phone) {
    }

    public SignUpActivity() {
    }


    /**
     * Allows the user to sign up and create an account on the app.
     * Allows user and player to the the registration of the their account
     * Connects and links with FireBase FireStore to store the data
     * Getting the preferences on the location if the user wishes to share
     * At this point, the app also collects the player's personal information to create the account
     * Personal information includes: username, phonenumer, email
     * The user id will be generated by the app and giving it to the user
     * @param savedInstanceState  a package handles the sign up activity from user
     * @see FirebaseFirestore
     * @see Player
     * @see com.example.qr_project.utils.QR_Code
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpButton = findViewById(R.id.submit_sign_up_button);
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneNumberEditText = findViewById(R.id.number_edit_text);

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private boolean deletingHyphen;
            private boolean deletingBackward;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }

                isFormatting = true;

                String phoneNumber = s.toString().replaceAll("[^\\d]", "");
                String formattedNumber = formatPhoneNumber(phoneNumber);

                int selectionStart = phoneNumberEditText.getSelectionStart();
                int selectionEnd = phoneNumberEditText.getSelectionEnd();

                phoneNumberEditText.setText(formattedNumber);
                phoneNumberEditText.setSelection(selectionStart + getSelectionDelta(formattedNumber, phoneNumber, selectionStart, deletingHyphen, deletingBackward));

                isFormatting = false;
            }

            private int getSelectionDelta(String formattedNumber, String phoneNumber, int selectionStart, boolean deletingHyphen, boolean deletingBackward) {
                int formattedLen = formattedNumber.length();
                int phoneLen = phoneNumber.length();
                int selectionDelta = formattedLen - phoneLen;
                if (deletingHyphen && selectionStart > 0) {
                    selectionDelta--;
                }
                if (deletingBackward && selectionStart > 1) {
                    selectionDelta++;
                }
                if (selectionStart + selectionDelta > formattedLen) {
                    selectionDelta = formattedLen - selectionStart;
                }
                if (selectionStart + selectionDelta < 0) {
                    selectionDelta = -selectionStart;
                }
                return selectionDelta;
            }

        });

        db = FirebaseFirestore.getInstance();

        CollectionReference usersCol = FirebaseFirestore.getInstance().collection("users");

        signUpButton.setOnClickListener(v -> {
            // Get the information that the user entered
            String userID = generateUserID();
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();

            // All entries are non-empty, proceed
            if (!(username.isEmpty())) {
                // Use the get() method to retrieve all documents in the collection
                usersCol.get().addOnCompleteListener(task -> {
                    boolean invalid = true;
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the values of the "username", "email", and "phoneNumber" fields
                            String usernameDB = document.getString("username");
                            if (username.equals(usernameDB)) {
                                Toast.makeText(this, "That username is taken", Toast.LENGTH_SHORT)
                                        .show();
                                invalid = false;
                                break;
                            }

                            String emailDB = document.getString("email");
                            if (email.equals(emailDB) && !(email.isEmpty())) {
                                Toast.makeText(this, "That email is taken", Toast.LENGTH_SHORT)
                                        .show();
                                invalid = false;
                                break;
                            }

                            String phoneNumberDB = document.getString("phoneNumber");
                            if (phoneNumber.equals(phoneNumberDB) && !(phoneNumber.isEmpty())) {
                                Toast.makeText(this, "That phone number is taken", Toast.LENGTH_SHORT)
                                        .show();
                                invalid = false;
                                break;
                            }

                            if (phoneNumber.length() < 14 && phoneNumber.length() > 0) {
                                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT)
                                        .show();
                                invalid = false;
                                break;
                            }
                        }
                        if (invalid) {
                            // Create a new user with the information
                            Player user = new Player(username, email, phoneNumber, userID);
                            db.collection("users").document(userID).set(user);
                            SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("user_id", userID);
                            editor.apply();

                            // Store userID
                            Intent intent = new Intent();
                            intent.putExtra("userId", userID);

                            // Result code 0 indicating sign up complete
                            setResult(0, intent);
                            finish();
                        }
                    } else {
                        Log.d("My Tag", "Error getting documents: ", task.getException());
                    }
                });

            }
            // Some entries were empty, raise a toast message w/ warning
            else {
                Toast.makeText(this, "Please fill in username", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }


        /**
         * Generates a new user ID
         * @return a new user ID
         */
    public String generateUserID() {
        // Generate a new UUID
        UUID uuid = UUID.randomUUID();
        // Convert the UUID to a string and return it
        return uuid.toString();
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 4) {
            return phoneNumber;
        } else if (phoneNumber.length() < 7) {
            return String.format("(%s) %s",
                    phoneNumber.substring(0, 3),
                    phoneNumber.substring(3, phoneNumber.length()));
        } else {
            return String.format("(%s) %s-%s",
                    phoneNumber.substring(0, 3),
                    phoneNumber.substring(3, 6),
                    phoneNumber.substring(6, phoneNumber.length()));
        }
    }

    // Everything below this line could be deleted if necessary
    // They are meant for testing
    public void SignUpClick(View view) {
        Intent intent = new Intent(SignUpActivity.this, UserHomeActivity.class);
        startActivity(intent);
    }

    public void add(SignUpActivity mockUserList) {
    }

    public Map<Object, Object> getString() {
        return null;
    }

    public char compareTo(Object o) {
        return 0;
    }


}