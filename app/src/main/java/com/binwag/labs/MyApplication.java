package com.binwag.labs;

import io.realm.Realm;

public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {

        super.onCreate();
        Realm.init(this);

    }
}
