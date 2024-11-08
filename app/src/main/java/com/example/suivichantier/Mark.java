package com.example.suivichantier;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(indices = {@Index("planID")},foreignKeys = {
        @ForeignKey(entity = Plan.class,
                parentColumns = "planID",
                childColumns = "planID",
                onDelete = ForeignKey.CASCADE)
})

public class Mark {

    @PrimaryKey @NonNull
    private String markID;
    private String designation;
    private String type;
    private String lot;
    private int posx;
    private int posy;
    private String observation;
    private String statut;
    private String priorite;
    private String date;
    private int planID; // Clé étrangère vers le plan associé

    //private boolean synchro;
    // Constructeurs, getters et setters


    public Mark(@NonNull String markID, String designation, String type, String lot, int posx, int posy, String observation, String statut, String priorite, String date, int planID) {
        this.markID=markID;
        this.designation=designation;
        this.type = type;
        this.lot = lot;
        this.posx = posx;
        this.posy = posy;
        this.observation = observation;
        this.statut = statut;
        this.priorite = priorite;
        this.date = date;
        this.planID = planID;
        //this.synchro=false;
    }

    public String getMarkID() {
        return markID;
    }

    public void setMarkID(String markID) {
        this.markID = markID;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosx() {
        return posx;
    }

    public void setPosx(int posx) {
        this.posx = posx;
    }

    public int getPosy() {
        return posy;
    }

    public void setPosy(int posy) {
        this.posy = posy;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    @Override
    public String toString() {
        return markID; // Cette méthode détermine ce qui est affiché dans le Spinner
    }

//    public boolean isSynchro() {
//        return synchro;
//    }

//    public void setSynchro(boolean synchro) {
//        this.synchro = synchro;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        return posx == mark.posx && posy == mark.posy && planID == mark.planID  && Objects.equals(markID, mark.markID) && Objects.equals(designation, mark.designation) && Objects.equals(type, mark.type) && Objects.equals(lot, mark.lot) && Objects.equals(observation, mark.observation) && Objects.equals(statut, mark.statut) && Objects.equals(priorite, mark.priorite) && Objects.equals(date, mark.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markID, designation, type, lot, posx, posy, observation, statut, priorite, date, planID);
    }
}
