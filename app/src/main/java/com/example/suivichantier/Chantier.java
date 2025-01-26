package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity( indices = {@Index("entrepriseproprietaireID")},
        foreignKeys = @ForeignKey(entity = Entreprise.class,
        parentColumns = "entrepriseID",
        childColumns = "entrepriseproprietaireID",
        onDelete = ForeignKey.CASCADE)
)

public class Chantier {
    @PrimaryKey
    private int chantierID;
    private String nom;
    private String localisation;
    private String dateDebut;
    private int entrepriseproprietaireID;

    public Chantier() {
    }
    @Ignore
    public Chantier(int chantierID, String nom, String localisation, String dateDebut, String dateFin, String etat, int entrepriseproprietaireID) {
        this.chantierID = chantierID;
        this.nom = nom;
        this.localisation = localisation;
        this.dateDebut = dateDebut;
        this.entrepriseproprietaireID = entrepriseproprietaireID;
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

    public int getEntrepriseproprietaireID() {
        return entrepriseproprietaireID;
    }

    public void setEntrepriseproprietaireID(int entrepriseproprietaireID) {
        this.entrepriseproprietaireID = entrepriseproprietaireID;
    }
}