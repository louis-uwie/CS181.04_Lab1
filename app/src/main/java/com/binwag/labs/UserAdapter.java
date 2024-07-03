package com.binwag.labs;

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
                    Log.d("Deletion", "Clicked UserAdapterDelete");
                    User userToDelete = (User) view.getTag();
                    activity.delete(userToDelete);
                }
            });

            //EDIT METHOD
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("EditButton", "Edit Button Pressed!");
                }
            });
        }
    }

}
