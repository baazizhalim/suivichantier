package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("planID")},foreignKeys = {
        @ForeignKey(entity = Plan.class,
                parentColumns = "planID",
                childColumns = "planID",
                onDelete = ForeignKey.CASCADE)
})
public class Mark {

    @PrimaryKey
    private int markID;
    private String type;
    private int posx;
    private int posy;
    private String observation;
    private String statut;
    private String priorite;
    private String date;
    private int planID; // Clé étrangère vers le plan associé

    // Constructeurs, getters et setters


    public Mark() {
    }
    @Ignore
    public Mark(String type, int posx, int posy, String observation, String statut, String priorite, String date, int planID) {
        this.type = type;
        this.posx = posx;
        this.posy = posy;
        this.observation = observation;
        this.statut = statut;
        this.priorite = priorite;
        this.date = date;
        this.planID = planID;
    }

    public int getMarkID() {
        return markID;
    }

    public void setMarkID(int markID) {
        this.markID = markID;
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
}
