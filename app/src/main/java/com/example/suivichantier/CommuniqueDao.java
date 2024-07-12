package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommuniqueDao {
    @Insert
    void insert(Communique communique );

    @Query("SELECT * FROM communique")
    List<Communique> getAllCommuniques();
}
