package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EntrepriseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Entreprise entreprise);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Entreprise> entreprises);

    @Query("SELECT * FROM entreprise")
    List<Entreprise> getAllEntreprises();

    @Query("SELECT * FROM entreprise where username=:username and password=:password")
    Entreprise login(String username,String password);
}

