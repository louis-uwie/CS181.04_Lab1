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

    RecyclerView recyclerView;
    Button clearRlmButton, addUsrButton;

    private Realm realm;
    private UserAdapter userAdapter;
    User currentUser;

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

    public void checkPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .withListener(new BaseMultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            init();
                        } else {
                            toastRequirePermissions();
                        }
                    }
                })
                .check();
    }

    public void toastRequirePermissions() {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }

    private void init() {
        recyclerView = findViewById(R.id.rvUsers);
        clearRlmButton = findViewById(R.id.btnClearRealm);
        addUsrButton = findViewById(R.id.btnAddRealmUsr);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        realm = Realm.getDefaultInstance();

        userAdapter = new UserAdapter(this, getAllUsers());
        recyclerView.setAdapter(userAdapter);

        clearRlmButton.setOnClickListener(v -> {
            Log.d("Admin Functions", "Clear Realm Button Clicked");
            clearRealmUser();
        });

        addUsrButton.setOnClickListener(v -> {
            Log.d("Admin Functions", "Add User Button Clicked");
            Intent registerActivity = new Intent(UserManagement.this, RegisterActivity.class);
            startActivity(registerActivity);
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
        realm.executeTransactionAsync(realm -> {
            realm.deleteAll();
            Log.d("Admin Functions", "Cleared Realm");
        });
    }

    public void delete(User userToDelete) {
        Log.d("Admin Functions", "Delete User - Clicked");

        String userUuid = userToDelete.getUuid();

        realm.executeTransactionAsync(realm -> {
            User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();
            if (user != null) {
                user.deleteFromRealm();
                Log.d("Admin Functions", "Delete User - Successful");
            }
        });
        Log.d("Admin Functions", "Delete User - Out");
    }

    public void takePhoto(User user) {
        currentUser = user;
        Log.d("Image Function", "takePhoto: currentUser set to " + user.getUuid());
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == 0 && responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
            byte[] jpeg = data.getByteArrayExtra("rawJpeg");

            if (currentUser != null) {
                try {
                    realm.executeTransaction(realm -> {
                        String imageUrl = System.currentTimeMillis() + ".jpeg";
                        currentUser.setImageUrl(imageUrl);

                        File savedImage = saveFile(jpeg, imageUrl);
                        if (savedImage != null) {
                            Log.d("Image Function", "Image saved at: " + savedImage.getAbsolutePath());
                            realm.copyToRealmOrUpdate(currentUser);
                        } else {
                            Log.d("Image Function", "Failed to save image.");
                        }
                    });

                    // Notify the adapter of changes
                    userAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Image Function", "Error: " + e);
                } finally {
                    currentUser = null; // Reset currentUser after handling
                }
            } else {
                Log.d("Image Function", "currentUser is null when processing the image.");
            }
        }
    }

    private File saveFile(byte[] jpeg, String name) {
        File getImageDir = getExternalCacheDir();
        File savedImage = new File(getImageDir, name);

        try (FileOutputStream fos = new FileOutputStream(savedImage)) {
            fos.write(jpeg);
            Log.d("Image Function", "File saved to: " + savedImage.getAbsolutePath());
            return savedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
