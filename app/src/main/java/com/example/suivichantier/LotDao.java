package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lot lot);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Lot> lots);

    @Query("SELECT * FROM lot")
    List<Lot> getAllLots();

    @Query("SELECT * FROM lot where lotID=:lotID")
    List<Lot> getAllLotIDs(String lotID);

    @Query("SELECT * FROM lot where entrepriseRealisationID=:id")
    List<Lot> getAllLotER(int id);

    @Query("SELECT * FROM lot where entrepriseSuiviID=:id and chantierID=:cid")
    List<Lot> getAllLotChantierES(int id,int cid);

    @Query("SELECT * FROM lot where entrepriseRealisationID=:id and chantierID=:cid")
    List<Lot> getAllLotChantierER(int id,int cid);

    @Query("SELECT * FROM lot where entrepriseSuiviID=:id")
    List<Lot> getAllLotES(int id);

    @Query("SELECT * FROM lot where chantierID = :id")
    List<Lot> getAllLotChantier(int id);

    @Query("SELECT * FROM lot where entrepriseRealisationID=:eid and chantierID=:cid and description=:desc")
    List<Lot> getLotER(int eid,int cid, String desc);

    @Query("SELECT * FROM lot where entrepriseSuiviID=:eid and chantierID=:cid and description=:desc")
    List<Lot> getLotES(int eid,int cid, String desc);

    @Query("SELECT * FROM lot where chantierID=:cid and description=:desc")
    List<Lot> getLot(int cid, String desc);
}
