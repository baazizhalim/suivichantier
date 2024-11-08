package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Delete;
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

    @Query("SELECT * FROM photo where markID=:markID")
    List<Photo> getAllPhotosMark(String markID);

    @Query("SELECT * FROM photo where photoID=:photoID")
    Photo getOnePhoto(int photoID);

    @Query("delete FROM photo where file=:file")
    int deletePhoto(String file);

    @Query("SELECT file FROM photo where markID=:markID")
    List<String> getAllfilesMark(String markID);

    @Delete
    int delete(Photo photo);
}
