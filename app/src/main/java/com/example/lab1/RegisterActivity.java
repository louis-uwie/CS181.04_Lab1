package com.example.lab1;

import android.content.SharedPreferences;
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

public class RegisterActivity extends AppCompatActivity {

    EditText NewUsername, NewPassword, ConfirmNewP;
    Button SaveButton, CancelButton;

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

        SharedPreferences myAccounts = getSharedPreferences("myAccounts", MODE_PRIVATE);

        NewUsername = findViewById(R.id.etNewUsername);
        NewPassword = findViewById(R.id.etNewPassword);
        ConfirmNewP = findViewById(R.id.etConfirmNewP);

        SaveButton = findViewById(R.id.btnSave);
        CancelButton = findViewById(R.id.btnCancel);

         SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newUser = NewUsername.getText().toString();
                String newPass = NewPassword.getText().toString();
                String confPass = ConfirmNewP.getText().toString();

                if(newUser.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Name must not be blank.", Toast.LENGTH_SHORT).show();
                } else if(newPass.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"New Password must not be blank.", Toast.LENGTH_SHORT).show();
                } else if (!newPass.equals(confPass)) {
                    Toast.makeText(RegisterActivity.this, "Confirm password does not match.", Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences.Editor editor = myAccounts.edit();
                    editor.putString("username", newUser);
                    editor.putString("password", newPass);
                    editor.apply();

                    Toast.makeText(RegisterActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                    finish();
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
}

