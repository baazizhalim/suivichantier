package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("entrepriseID")},foreignKeys = @ForeignKey(entity = Entreprise.class,
        parentColumns = "entrepriseID",
        childColumns = "entrepriseID",
        onDelete = ForeignKey.CASCADE))

public class Chantier {
    @PrimaryKey
    private int chantierID;

    private String nom;
    private String localisation;
    private String dateDebut;
    private String dateFin;
    private String etat;
    private int entrepriseID;

    public Chantier() {
    }
    @Ignore
    public Chantier(int chantierID, String nom, String localisation, String dateDebut, String dateFin, String etat, int entrepriseID) {
        this.chantierID = chantierID;
        this.nom = nom;
        this.localisation = localisation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etat = etat;
        this.entrepriseID = entrepriseID;
    }

    // Getters et Setters
    public int getChantierID() {
        return chantierID;
    }

    public void setChantierID(int chantierID) {
        this.chantierID = chantierID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getEntrepriseID() {
        return entrepriseID;
    }

    public void setEntrepriseID(int entrepriseID) {
        this.entrepriseID = entrepriseID;
    }
}