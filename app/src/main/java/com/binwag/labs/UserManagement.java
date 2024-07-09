package com.binwag.labs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class UserManagement extends AppCompatActivity {

    RecyclerView recyclerView;
    Button clearRlmButton, addUsrButton;
    ImageView imageView; // ImageView for adding an image

    private Realm realm;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.rvUsers);
        clearRlmButton = findViewById(R.id.btnClearRealm);
        addUsrButton = findViewById(R.id.btnAddRealmUsr);

        // initialize RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // initialize Realm
        realm = Realm.getDefaultInstance();

        // set up the adapter
        userAdapter = new UserAdapter(this, getAllUsers());
        recyclerView.setAdapter(userAdapter);

        clearRlmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Admin Functions", "Clear Realm Button Clicked");
                clearRealmUser();
            }
        });

        addUsrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Admin Functions", "Add User Button Clicked");
                Intent registerActivity = new Intent(UserManagement.this, RegisterActivity.class);
                startActivity(registerActivity);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private RealmResults<User> getAllUsers() {
        return realm.where(User.class).findAll();
    }

    private void clearRealmUser() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.deleteAll();
                Log.d("Admin Functions", "Cleared Realm");
            }
        });
    }

    public void delete(User userToDelete) {
        String userUuid = userToDelete.getUuid(); // Assuming you have a method to get UUID of the user

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                // Query the Realm for the user with the matching UUID
                User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();
                if (user != null) {
                    // Delete the user from Realm
                    user.deleteFromRealm();
                    Log.d("Admin Functions", "Deleted user with UUID: " + userUuid);
                } else {
                    Log.d("Admin Functions", "User with UUID not found: " + userUuid);
                }
            }
        });
    }

}
