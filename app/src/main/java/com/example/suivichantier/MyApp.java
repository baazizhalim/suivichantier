package com.example.suivichantier;

import android.app.Application;

import androidx.room.Room;

public class MyApp extends Application {
    private AppDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "my-database").build();
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }
}
