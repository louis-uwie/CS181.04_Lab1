package com.binwag.labs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class WelcomeActivity extends AppCompatActivity {

    TextView wcText;
    Button exit;
    SharedPreferences myAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myAccounts = getSharedPreferences("myAccounts", MODE_PRIVATE);

        String savedUuid = myAccounts.getString("uuid", null);
        if (savedUuid != null) {
            User user = Realm.getDefaultInstance().where(User.class)
                    .equalTo("uuid", savedUuid)
                    .findFirst();
            if (user != null) {
                wcText = findViewById(R.id.tvWelcome);
                wcText.setText("Welcome, " + user.getName() + "!");

                exit = findViewById(R.id.btnExit);
                exit.setOnClickListener(v -> finish());
            } else {
                wcText.setText("Welcome!");
            }
        } else {
            wcText.setText("Welcome!");
        }
    }
}
