package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlanExecutionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlanExecution planExecution );

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlanExecution> planExecutions);

    @Query("SELECT * FROM planexecution")
    List<PlanExecution> getAllPlanExecutions();

    @Query("delete FROM planexecution")
    void deleteAll();

    @Query("SELECT * FROM planexecution where chantierID=:chantierID")
    List<PlanExecution> getAllPlanExecutions(int chantierID);
}
