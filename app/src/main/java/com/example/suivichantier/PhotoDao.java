package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Photo photo );
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Photo> photos);
    @Query("SELECT * FROM photo")
    List<Photo> getAllPhotos();
}
