package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnexeReserves extends AppCompatActivity {
    private AppDatabase mDatabase;
    private List<Mark> marks=new ArrayList<>();
    private int chantierID;
    private String nomClient;
    private String nomChantier;
    private int entrepriseID;
    private String nomEntreprise;
    private String typeEntreprise;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annexe_reserves);
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
         entrepriseID = intent.getIntExtra("entrepriseID",0);
         nomEntreprise = intent.getStringExtra("nomEntreprise");
         typeEntreprise = intent.getStringExtra("typeEntreprise");
         nomChantier = intent.getStringExtra("nomChantier");
         nomClient = intent.getStringExtra("nomClient");
         chantierID = intent.getIntExtra("chantierID",0);

        if(Objects.equals(typeEntreprise, "client")){
            marks=mDatabase.markDao().getAllMarksClient(chantierID);


        } else if(Objects.equals(typeEntreprise, "ES")) {
            marks = mDatabase.markDao().getAllMarksES(entrepriseID);

        }else {
            marks = mDatabase.markDao().getAllMarksER(entrepriseID);

        }

List <Mark> markReserves=marks.stream().filter(mark->mark.getType().equals("reserve")).collect(Collectors.toList());


        MyAdapterReserves adapter = new MyAdapterReserves(this,markReserves,entrepriseID, nomEntreprise, typeEntreprise, nomClient,nomChantier,chantierID);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.putExtra("nomEntreprise", nomEntreprise);
        intent.putExtra("entrepriseID", entrepriseID);
        intent.putExtra("typeEntreprise", typeEntreprise);
        intent.putExtra("chantierID", chantierID);
        intent.putExtra("nomChantier", nomChantier);


        startActivity(intent);
        finish();
        return true;
    }

}


