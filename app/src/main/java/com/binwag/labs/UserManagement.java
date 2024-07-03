package com.binwag.labs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    /**
     * TODO:
     * Lab 3:  RecyclerView and Realm
     * User Management ★
     *      Using your previous, replace the “Register” with an “Admin” Button,
     *      this will open the User Management screen ★
     * Admin Screen ★
     *      Contains a RecyclerView where each row contains: ★
     *      -	name ★
     *      -	password ★
     *      -	A button to delete the row ★
     *      -	A button to edit the row (click this will open a screen similar to the Register Activity)
     * Below the RecyclerView are two buttons ★
     *      -	Add – opens a new Register UI to add to the list (this is where the old Register functionality will go) ★
     *      -	Clear  – clears all the current users from Realm ★
     * Bonus:
     *      -   Add a prompt to confirm yes or no on delete. ★
     */

    RecyclerView recyclerView;
    Button clearRlmButton, addUsrButton;

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
        Log.d("Admin Functions", "Delete User - Clicked");

        String userUuid = userToDelete.getUuid();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();
                if (user != null) {
                    user.deleteFromRealm();
                    Log.d("Admin Functions", "Delete User - Successfull");
                }
            }
        });

        Log.d("Admin Functions", "Delete User - Out");
    }
}
