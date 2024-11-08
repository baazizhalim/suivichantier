package com.example.suivichantier;
import androidx.room.Database;
import androidx.room.RoomDatabase;



@Database(entities = {Entreprise.class, Chantier.class, Lot.class, Plan.class,Mark.class, Photo.class, Remarque.class, PlanExecution.class, Pv.class, Communique.class}, version = 7,exportSchema=false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntrepriseDao entrepriseDao();
    public abstract ChantierDao chantierDao();
    public abstract LotDao lotDao();
    public abstract PlanDao planDao();
    public abstract MarkDao markDao();
    public abstract PhotoDao photoDao();
    public abstract RemarqueDao remarqueDao();
    public abstract PlanExecutionDao planExecutionDao();
    public abstract PvDao pvDao();
    public abstract CommuniqueDao communiqueDao();

}

