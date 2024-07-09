package com.binwag.labs;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.UserViewHolder> {

    /**
     * Adding Camera functionality checklist
     *
     * 1) copy Android-Image_Cropper.aar to app/libs in Project View
     * 2) update gradle files
     * 	- cam deps and karumi deps
     * 	- targetSDK 29 (for compatibility with the AndroidImageCropper, 30+ will result in nothing happening)
     *
     * 3) update manifest
     * 	- add permissions
     * 	- add FileProvider entry
     * 	- copy fileprovider.xml to /xml folder
     * 	- add fileAuthority entry in strings.xml (make sure it is the same as your package name)
     * 	- add ImageActivity_ entry
     *
     * 4) copy ImageActivity.java to src/package
     * 5) copy activity_image_capture.xml to res/layout
     */


    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        TextView passwordTextView;
        Button deleteButton;
        Button editButton;


        public UserViewHolder(@NonNull View itemView){
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tvUserName);
            passwordTextView = itemView.findViewById(R.id.tvUserPassword);

            deleteButton = itemView.findViewById(R.id.btnDelete);
            editButton = itemView.findViewById(R.id.btnEdit);
        }
    }

    UserManagement activity;


    public UserAdapter(UserManagement activity, OrderedRealmCollection<User> data) {
        super(data, true);
        this.activity = activity;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_adapter, parent, false);

        UserViewHolder userViewHolder = new UserViewHolder(view);
        return userViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);

        if (user != null) {
            holder.nameTextView.setText(user.getName());
            holder.passwordTextView.setText(user.getPassword());

            //DELETE METHOD
            holder.deleteButton.setTag(user);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Admin Functions", "Delete Button Clicked");
                    User userToDelete = (User) view.getTag();
                    activity.delete(userToDelete);
                }
            });

            //EDIT METHOD
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Admin Functions", "Edit User Button Clicked");
                    Intent editIntent = new Intent(activity, EditUser.class);
                    editIntent.putExtra("userId", user.getUuid());
                    activity.startActivity(editIntent);
                }
            });
        }
    }
}
