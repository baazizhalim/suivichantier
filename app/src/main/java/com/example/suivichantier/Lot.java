package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("chantierID"),@Index("entrepriseSuiviID"),@Index("entrepriseRealisationID")},
        foreignKeys = {
        @ForeignKey(entity = Chantier.class,
                parentColumns = "chantierID",
                childColumns = "chantierID",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Entreprise.class,
                parentColumns = "entrepriseID",
                childColumns = "entrepriseSuiviID",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Entreprise.class,
                parentColumns = "entrepriseID",
                childColumns = "entrepriseRealisationID",
                onDelete = ForeignKey.CASCADE)
})
public class Lot {
    @PrimaryKey
    private int lotID;

    private String description;
    private int chantierID;
    private int entrepriseSuiviID;
    private int entrepriseRealisationID;

    public Lot() {
    }
    @Ignore
    public Lot(int lotID, String description, int chantierID, int entrepriseSuiviID, int entrepriseRealisationID) {
        this.lotID = lotID;
        this.description = description;
        this.chantierID = chantierID;
        this.entrepriseSuiviID = entrepriseSuiviID;
        this.entrepriseRealisationID = entrepriseRealisationID;
    }

    // Getters et Setters
    public int getLotID() {
        return lotID;
    }

    public void setLotID(int lotID) {
        this.lotID = lotID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChantierID() {
        return chantierID;
    }

    public void setChantierID(int chantierID) {
        this.chantierID = chantierID;
    }

    public int getEntrepriseSuiviID() {
        return entrepriseSuiviID;
    }

    public void setEntrepriseSuiviID(int entrepriseSuiviID) {
        this.entrepriseSuiviID = entrepriseSuiviID;
    }

    public int getEntrepriseRealisationID() {
        return entrepriseRealisationID;
    }

    public void setEntrepriseRealisationID(int entrepriseRealisationID) {
        this.entrepriseRealisationID = entrepriseRealisationID;
    }
}