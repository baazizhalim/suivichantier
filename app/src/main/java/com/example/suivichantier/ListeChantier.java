package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListeChantier extends AppCompatActivity {
    private AppDatabase mDatabase;
    private RecyclerView recyclerView;
    private MyAdapterChantier myAdapterChantier;
    private List<ListItemChantier> itemListChantier;
    private List<Chantier> chantiers=new ArrayList<>();
    private TextView titre ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_chantier);
        chantiers.clear();
        itemListChantier = new ArrayList<>();// Initialiser la liste des éléments
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        titre = findViewById(R.id.titre);
        Intent intent = getIntent();
        int entrepriseID = intent.getIntExtra("entrepriseID",0);
        String nomEntreprise = intent.getStringExtra("nomEntreprise");
        String typeEntreprise = intent.getStringExtra("typeEntreprise");
        String t= titre.getText().toString();
        titre.setText(t+" de " +nomEntreprise);


        if(Objects.equals(typeEntreprise, "client")){
            chantiers=mDatabase.chantierDao().getAllChantiers(entrepriseID);


        } else if(typeEntreprise.equals("ES")) {
            List<Lot> lots = mDatabase.lotDao().getAllLotES(entrepriseID);
            for (Lot lot : lots) {
                Chantier chantier=mDatabase.chantierDao().getChantierByID(lot.getChantierID());


                if(chantiers.stream().noneMatch(chant -> chant.getChantierID() == chantier.getChantierID()))chantiers.add(chantier);

            }
        }else {
            List<Lot> lots = mDatabase.lotDao().getAllLotER(entrepriseID);
            for (Lot lot : lots) {
                Chantier chantier = mDatabase.chantierDao().getChantierByID(lot.getChantierID());
                if(chantiers.stream().noneMatch(chant -> chant.getChantierID() == chantier.getChantierID()))chantiers.add(chantier);


            }
        }

            chantiers.forEach(chantier -> {
                itemListChantier.add(new ListItemChantier(chantier.getNom(), chantier.getChantierID()));
            });



        myAdapterChantier = new MyAdapterChantier(this, itemListChantier,entrepriseID,nomEntreprise,typeEntreprise);
        recyclerView.setAdapter(myAdapterChantier);
    }
}

