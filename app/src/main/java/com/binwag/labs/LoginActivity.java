package com.binwag.labs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton, registerButton, clearButton, adminButton;
    CheckBox rememberMe;
    SharedPreferences myAccounts;
    Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();
        myAccounts = getSharedPreferences("myAccounts", MODE_PRIVATE);

        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);

        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.btnRegister);
        clearButton = findViewById(R.id.btnClear);
        adminButton = findViewById(R.id.btnAdmin);

        rememberMe = findViewById(R.id.cbRememberMe);

        if (myAccounts.getBoolean("rememberMe", false)) {
            String savedUsername = myAccounts.getString("username", "");
            String savedPassword = myAccounts.getString("password", "");

            usernameInput.setText(savedUsername);
            passwordInput.setText(savedPassword);
            rememberMe.setChecked(true);
        }

        registerButton.setOnClickListener(v -> register());
        clearButton.setOnClickListener(v -> clear());
        loginButton.setOnClickListener(v -> login());
        adminButton.setOnClickListener(v -> adminLog());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }


    public void login() {
        String inputUsername = usernameInput.getText().toString();
        String inputPassword = passwordInput.getText().toString();

        User user = realm.where(User.class)
                .equalTo("name", inputUsername)
                .findFirst();

        if (user != null) {
            // User found, check password
            if (user.getPassword().equals(inputPassword)) {
                // Password matches
                handleSuccessfulLogin(user);
            } else {
                // Password doesn't match
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        } else {
            // No user found with the given name
            Toast.makeText(this, "No User found", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleSuccessfulLogin(User user) {
        Toast.makeText(this, "Login Successful.", Toast.LENGTH_SHORT).show();

        saveUuid(user.getUuid());

        if (rememberMe.isChecked()) {
            saveRememberMeState(true);
            saveCredentials(user.getName(), user.getPassword());
        } else {
            saveRememberMeState(false);
            clearCredentials();
        }
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }


    public void adminLog() {
        Intent adminActivity = new Intent(this, UserManagement.class);
        startActivity(adminActivity);
    }


    public void register() {
        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivity(registerActivity);

        usernameInput.setText("");
        passwordInput.setText("");
    }


    public void clear() {
        clearCredentials();
        Toast.makeText(this, "Shared Preferences Cleared", Toast.LENGTH_SHORT).show();
    }


    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = myAccounts.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }


    private void saveRememberMeState(boolean isChecked) {
        SharedPreferences.Editor editor = myAccounts.edit();
        editor.putBoolean("rememberMe", isChecked);
        editor.apply();
    }


    private void clearCredentials() {
        SharedPreferences.Editor editor = myAccounts.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("rememberMe");
        editor.apply();
    }


    private void saveUuid(String uuid) {
        SharedPreferences.Editor editor = myAccounts.edit();
        editor.putString("uuid", uuid);
        editor.apply();
    }
}