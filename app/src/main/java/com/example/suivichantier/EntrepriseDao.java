package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EntrepriseDao {
    @Insert
    void insert(Entreprise entreprise);

    @Query("SELECT * FROM entreprise")
    List<Entreprise> getAllEntreprises();

    @Query("SELECT * FROM entreprise where username=:username and password=:password")
    Entreprise login(String username,String password);
}

