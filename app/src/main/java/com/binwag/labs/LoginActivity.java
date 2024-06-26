package com.binwag.labs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton, registerButton, clearButton;
    CheckBox rememberMe;
    SharedPreferences myAccounts;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Realm
        realm = Realm.getDefaultInstance();

        // Initialize UI elements
        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.btnRegister);
        clearButton = findViewById(R.id.btnClear);
        rememberMe = findViewById(R.id.cbRememberMe);

        // Set onClick listeners
        registerButton.setOnClickListener(v -> register());
        clearButton.setOnClickListener(v -> clear());
        loginButton.setOnClickListener(v -> login());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the Realm instance when the Activity is destroyed
        if (realm != null) {
            realm.close();
        }
    }

    public void login() {
        String inputUsername = usernameInput.getText().toString();
        String inputPassword = passwordInput.getText().toString();

        // Query Realm for the user with matching username and password
        User user = realm.where(User.class)
                .equalTo("name", inputUsername)
                .equalTo("password", inputPassword)
                .findFirst();

        if (user != null) {
            // Login successful
            handleSuccessfulLogin(user);
        } else {
            // Invalid credentials
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSuccessfulLogin(User user) {
        Toast.makeText(this, "Login Successful.", Toast.LENGTH_SHORT).show();

        // Save user's UUID instead of username/password in SharedPreferences
        saveUuid(user.getUuid());

        // Handle rememberMe checkbox
        if (rememberMe.isChecked()) {
            saveCredentials(user.getName(), user.getPassword());
        } else {
            clearCredentials();
        }

        // Proceed to WelcomeActivity
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    public void register() {
        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivity(registerActivity);
    }

    public void clear() {
        clearCredentials();
        Toast.makeText(this, "Shared Preferences Cleared", Toast.LENGTH_SHORT).show();
    }

    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = myAccounts.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    private void clearCredentials() {
        SharedPreferences.Editor editor = myAccounts.edit();
        editor.clear().apply();
        usernameInput.setText("");
        passwordInput.setText("");
    }

    private void saveUuid(String uuid) {
        SharedPreferences.Editor editor = myAccounts.edit();
        editor.putString("uuid", uuid);
        editor.apply();
    }
}
