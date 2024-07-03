package com.binwag.labs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class EditUser extends AppCompatActivity {

    EditText editUser, editPass, confirmEditPass;
    Button saveChanges, cancelChanges;

    private Realm realm;
    private String userId; // Variable to store userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();

        if (getIntent() != null && getIntent().hasExtra("userId")) {
            userId = getIntent().getStringExtra("userId");
        }

        editUser = findViewById(R.id.etEditUsername);
        editPass = findViewById(R.id.etEditPassword);
        confirmEditPass = findViewById(R.id.etConfirmNewP);

        User user = realm.where(User.class).equalTo("uuid", userId).findFirst();
        if (user != null) {
            editUser.setText(user.getName());
            editPass.setText(user.getPassword());
        }

        saveChanges = findViewById(R.id.btnSaveEdit);
        cancelChanges = findViewById(R.id.btnCancelEdit);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedUsername = editUser.getText().toString().trim();
                String editedPassword = editPass.getText().toString().trim();
                String confirmedPassword = confirmEditPass.getText().toString().trim();

                if (!editedPassword.equals(confirmedPassword)) {
                    Toast.makeText(EditUser.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateUserDetails(userId, editedUsername, editedPassword);

            }
        });

        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUserDetails(String userId, String editedUsername, String editedPassword) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("uuid", userId).findFirst();
                if (user != null) {
                    user.setName(editedUsername);
                    user.setPassword(editedPassword);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditUser.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
