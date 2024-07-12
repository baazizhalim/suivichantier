package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlanExecutionDao {
    @Insert
    void insert(PlanExecution planExecution );

    @Query("SELECT * FROM planexecution")
    List<PlanExecution> getAllPlanExecutions();
}
