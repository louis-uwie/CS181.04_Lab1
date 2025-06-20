package com.binwag.labs;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.UserViewHolder> {

    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView, passwordTextView;
        Button deleteButton, editButton;
        ImageView imageView;

        public UserViewHolder(@NonNull View itemView){
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tvUserName);
            passwordTextView = itemView.findViewById(R.id.tvUserPassword);

            deleteButton = itemView.findViewById(R.id.btnDelete);
            editButton = itemView.findViewById(R.id.btnEdit);

            imageView = itemView.findViewById(R.id.imageView);
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

            // DELETE METHOD
            holder.deleteButton.setTag(user);
            holder.deleteButton.setOnClickListener(view -> {
                Log.d("Admin Functions", "Delete Button Clicked");
                User userToDelete = (User) view.getTag();
                activity.delete(userToDelete);
            });

            // EDIT METHOD
            holder.editButton.setOnClickListener(v -> {
                Log.d("Admin Functions", "Edit User Button Clicked");
                Intent editIntent = new Intent(activity, EditUser.class);
                editIntent.putExtra("userId", user.getUuid());
                activity.startActivity(editIntent);
            });

            holder.imageView.setOnClickListener(view -> activity.takePhoto(user));

            // Load the user image
            File getImageDir = activity.getExternalCacheDir();
            if (user.getImageUrl() != null) {
                File file = new File(getImageDir, user.getImageUrl());

                if (file.exists()) {
                    // Load the image into the ImageView using Picasso
                    Picasso.get()
                            .load(file)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(holder.imageView);
                } else {
                    // Use a default picture if the file doesn't exist
                    holder.imageView.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                // Use a default picture if the user has no image
                holder.imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}
