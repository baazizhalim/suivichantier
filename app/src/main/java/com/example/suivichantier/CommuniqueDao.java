package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommuniqueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Communique communique );
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Communique> communiques);
    @Query("SELECT * FROM communique")
    List<Communique> getAllCommuniques();

    @Query("SELECT * FROM communique where chantierID=:chantierID")
    List<Communique> getAllCommuniques(int chantierID);
}
