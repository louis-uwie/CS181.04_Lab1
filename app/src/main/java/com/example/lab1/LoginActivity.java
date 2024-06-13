package com.example.lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton, registerButton, clearButton;
    CheckBox rememberMe;
    SharedPreferences myAccounts = getSharedPreferences("myAccounts", MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.btnRegister);
        clearButton = findViewById(R.id.btnClear);

        rememberMe = findViewById(R.id.cbRememberMe);

        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);

        //TODO: SHARED PREFERENCES on 'myAccounts'

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

    }


    public void login(){


    }

    public void register(){

        Intent registerActivity = new Intent(this, RegisterActivity.class);

        startActivity(registerActivity);

    }

    public void clear(){

        myAccounts.edit().clear().apply();

    }
}