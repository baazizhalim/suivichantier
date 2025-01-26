package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("markID")}, foreignKeys = {
        @ForeignKey(entity = Mark.class,
                parentColumns = "markID",
                childColumns = "markID",
                onDelete = ForeignKey.CASCADE),
       })
public class Remarque {

    @PrimaryKey
    private int remarqueID;
    private String texte;
    private String date;
    private String markID; // Clé étrangère vers la mark associé
    private int entrepriseID; // Clé étrangère vers l'entrepise associé

    // Constructeurs, getters et setters


    public Remarque() {
    }
    @Ignore
    public Remarque(int remarqueID, String texte, String date, String markID, int entrepriseID) {
        this.remarqueID = remarqueID;
        this.texte = texte;
        this.date = date;
        this.markID = markID;
        this.entrepriseID = entrepriseID;
    }

    public int getRemarqueID() {
        return remarqueID;
    }

    public void setRemarqueID(int remarqueID) {
        this.remarqueID = remarqueID;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMarkID() {
        return markID;
    }

    public void setMarkID(String markID) {
        this.markID = markID;
    }

    public int getEntrepriseID() {
        return entrepriseID;
    }

    public void setEntrepriseID(int entrepriseID) {
        this.entrepriseID = entrepriseID;
    }
}
