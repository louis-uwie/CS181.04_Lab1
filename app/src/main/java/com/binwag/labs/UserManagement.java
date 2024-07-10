package com.binwag.labs;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

        checkPermissions();
    }

    public void checkPermissions()
    {

        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            // all permissions accepted proceed
                            init();
                        }
                        else
                        {
                            // notify about permissions
                            toastRequirePermissions();
                        }
                    }
                })
                .check();

    }

    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
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

    User currentUser;

    public void takePhoto(User a){
        currentUser = a;

        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i,0);
    }

    public void onAcativityResult(int requestCode, int responseCode, Intent data){
        super.onActivityResult(requestCode, responseCode, data);

        if(requestCode == 0){
            if(responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN){

                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try{
                    realm.beginTransaction();

                    currentUser.setImageUrl(System.currentTimeMillis()+".jpeg");

                    File savedImage = saveFile(jpeg, currentUser.getImageUrl());

                    realm.copyToRealmOrUpdate(currentUser);
                    realm.commitTransaction();
                } catch(Exception e){
                    e.printStackTrace();
                    Log.d("Image Function", "Error" + e);
                }
            }
        }
    }

    private File saveFile(byte[] jpeg, String name) throws IOException{
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, name);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }
}
