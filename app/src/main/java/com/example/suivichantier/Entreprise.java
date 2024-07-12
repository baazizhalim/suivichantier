package com.example.suivichantier;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.sql.Date;


@Entity
public class Entreprise {
    @PrimaryKey
    private int entrepriseID;

    private String nom;
    private String adresse;



    private String type;

    private String username;
    private String password;
    private String dateCreation;
    private String telephone;

    private int creerPar;

    public Entreprise() {
    }
    @Ignore
    public Entreprise(int entrepriseID, String nom, String adresse, String type, String username, String password, String dateCreation, String telephone, int creerPar) {
        this.entrepriseID = entrepriseID;
        this.nom = nom;
        this.adresse = adresse;
        this.type = type;
        this.username = username;
        this.password = password;
        this.dateCreation = dateCreation;
        this.telephone = telephone;
        this.creerPar = creerPar;
    }

    // Getters et Setters
    public int getEntrepriseID() {
        return entrepriseID;
    }

    public void setEntrepriseID(int entrepriseID) {
        this.entrepriseID = entrepriseID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getCreerPar() {
        return creerPar;
    }

    public void setCreerPar(int creerPar) {
        this.creerPar = creerPar;
    }
}


