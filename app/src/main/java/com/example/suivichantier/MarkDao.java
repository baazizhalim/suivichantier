package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.sql.Date;
import java.util.List;

@Dao
public interface MarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Mark mark );

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Mark> marks);

    @Query("SELECT * FROM mark")
    List<Mark> getAllMarks();

    @Query("SELECT * FROM mark where planID " +
                "in (select planID from `Plan` where lotID " +
                    "in ( select lotID from lot where chantierID=:chantierID )" +
                ");")
    List<Mark> getAllMarksClient(int chantierID);

    @Query("SELECT * FROM mark where planID " +
            "in (select planID from `Plan` where lotID " +
            "in ( select lotID from lot where entrepriseSuiviID=:entrepriseID )" +
            ");")
    List<Mark> getAllMarksES(int entrepriseID);

    @Query("SELECT * FROM mark where planID " +
            "in(select planID from `Plan` where lotID " +
            "in ( select lotID from lot where entrepriseRealisationID=:entrepriseID )" +
            ");")
    List<Mark> getAllMarksER(int entrepriseID);

    @Query("SELECT * FROM mark where markID=:markID")
    Mark getOneMark(String markID);

    @Query("SELECT * FROM mark where planID=:planID")
    List<Mark> getAllMarks(int planID);

    @Delete
    void delete(Mark mark );

    @Query("delete from mark where planID=:planID")
    void deleteAllMarks(int planID);

    @Update
    void update(Mark mark );

    @Query("SELECT * FROM mark where  lot=:lot  and date >= :dateDebut and date <= :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres1(String lot,String dateDebut,String dateFin, String planID);

    @Query("SELECT * FROM mark where statut=:etat and lot = :lot  and date>= :dateDebut and date <= :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres2(String etat, String lot, String dateDebut, String dateFin, String planID);

    @Query("SELECT * FROM mark where type=:type and lot=:lot  and date>= :dateDebut and date <= :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres3(String type,String lot,String dateDebut,String dateFin, String planID);

    @Query("SELECT * FROM mark where type=:type and lot=:lot and statut=:etat and date>= :dateDebut and date <= :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres4(String type,String etat,String lot,String dateDebut,String dateFin, String planID);

    @Query("SELECT * FROM mark where lot not in ('INFRA', 'SUPER', 'VRD') and date> :dateDebut and date < :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres5(String dateDebut, String dateFin, String planID);

    @Query("SELECT * FROM mark where type=:typeMark and lot not in ('INFRA', 'SUPER', 'VRD')  and date>= :dateDebut and date <= :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres6(String typeMark,  String dateDebut, String dateFin, String planID);

    @Query("SELECT * FROM mark where statut=:etat and lot not in ('INFRA', 'SUPER', 'VRD')  and date>= :dateDebut and date <= :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres7(String etat,String dateDebut,String dateFin, String planID);

    @Query("SELECT * FROM mark where type=:type and statut=:etat and lot not in ('INFRA', 'SUPER', 'VRD') and statut=:etat and date>= :dateDebut and date <= :dateFin and planID=:planID" )
    List<Mark> getMarkscriteres8(String type,String etat,String dateDebut,String dateFin, String planID);


    //lot not in ('INFRA', 'SUPER', 'VRD')


    @Query("SELECT lot as type,count(markID) as nbr FROM mark where type=:type and statut=:etat and date> :startDate and date < :endDate and planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) group by lot")
    List<ConsumptionData> getConsumptionBetweenDates1(String type,String etat, int chantierID, String startDate, String endDate);

    @Query("SELECT lot as type,count(markID) as nbr FROM mark where type=:type and date> :startDate and date < :endDate and planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) group by lot")
    List<ConsumptionData> getConsumptionBetweenDates2(String type, int chantierID, String startDate, String endDate);

    @Query("SELECT lot as type,count(markID) as nbr  FROM mark where  date> :startDate and date < :endDate and planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) group by lot")
    List<ConsumptionData> getConsumptionBetweenDates3(int chantierID, String startDate, String endDate);

    @Query("SELECT lot as type,count(markID) as nbr  FROM mark where  statut=:etat and date> :startDate and date < :endDate and planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) group by lot")
    List<ConsumptionData> getConsumptionBetweenDates4(String etat, int chantierID, String startDate, String endDate);

    @Query("SELECT count(markID) as nbr  FROM mark where  planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) ")
    int nbrMark( int chantierID);

    @Query("SELECT count(markID) as nbr  FROM mark where statut='SNT' and planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) ")
    int nbrMarkSignalee(int chantierID);

    @Query("SELECT count(markID) as nbr  FROM mark where  statut= 'TNV' and  planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) ")
    int nbrMarkTraiteeNonVerifee(int chantierID);

    @Query("SELECT count(markID) as nbr  FROM mark where  statut='TV'  and  planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) ")
    int nbrMarkTraiteeEtVerifee(int chantierID);


    @Query("SELECT count(markID) as nbr  FROM mark where  UPPER(lot)=UPPER(:lot)  and  planId in(select planID from `Plan` where lotID in (select lotID from lot where chantierId=:chantierID)) ")
    int nbrMarkByLot(int chantierID,String lot);

}
