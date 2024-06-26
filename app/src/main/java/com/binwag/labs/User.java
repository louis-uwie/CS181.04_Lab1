package com.binwag.labs;

import androidx.annotation.NonNull;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject{

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();;

    private String name;
    private String password;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", price=" + password +
                '}';
    }
}
