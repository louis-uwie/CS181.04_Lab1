package com.binwag.labs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;
import io.realm.RealmResults;

public class RegisterActivity extends AppCompatActivity {

    EditText NewUsername, NewPassword, ConfirmNewP;
    Button SaveButton, CancelButton;
    TextView labelHeader;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();

        NewUsername = findViewById(R.id.etEditUsername);
        NewPassword = findViewById(R.id.etEditPassword);
        ConfirmNewP = findViewById(R.id.etConfirmNewP);

        SaveButton = findViewById(R.id.btnSaveEdit);
        CancelButton = findViewById(R.id.btnCancelEdit);

        labelHeader = findViewById(R.id.tvEditUser);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            String editUserText = receivedIntent.getStringExtra("EditUser");
            if (editUserText != null) {
                labelHeader.setText(editUserText);
            }
        }


        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUser = NewUsername.getText().toString();
                String newPass = NewPassword.getText().toString();
                String confPass = ConfirmNewP.getText().toString();

                if (newUser.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Name must not be blank.", Toast.LENGTH_SHORT).show();
                } else if (newPass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "New Password must not be blank.", Toast.LENGTH_SHORT).show();
                } else if (!newPass.equals(confPass)) {
                    Toast.makeText(RegisterActivity.this, "Confirm password does not match.", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if user already exists
                    if (userExists(newUser)) {
                        Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Save new user to Realm
                        saveUserToRealm(newUser, newPass);
                    }
                }
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean userExists(String username) {
        RealmResults<User> results = realm.where(User.class).equalTo("name", username).findAll();
        return !results.isEmpty();
    }

    private void saveUserToRealm(String username, String password) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = new User();
                user.setName(username);
                user.setPassword(password);
                realm.copyToRealmOrUpdate(user);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Query Realm to get total number of users
                RealmResults<User> allUsers = realm.where(User.class).findAll();
                int totalUsers = allUsers.size();
                Toast.makeText(RegisterActivity.this, "New User saved. Total: " + totalUsers, Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(RegisterActivity.this, "Error saving user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
