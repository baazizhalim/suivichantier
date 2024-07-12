package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoDao {
    @Insert
    void insert(Photo photo );

    @Query("SELECT * FROM photo")
    List<Photo> getAllPhotos();
}
