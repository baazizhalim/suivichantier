package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Delete;
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

    @Query("SELECT * FROM entreprise where username=:username")
    Entreprise getUserInfo(String username);

    @Delete
    void delete(Entreprise entreprise);

    @Query ("select * from entreprise where entrepriseID=:entrepriseID")
    Entreprise select(int entrepriseID);

    @Query ("select nom from entreprise where entrepriseID in (select entrepriseproprietaireID from chantier where chantierID =:chantierID)")
    String getNameByChantierID(int chantierID);
}

