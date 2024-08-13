package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AnnexeReserves extends AppCompatActivity {
    private AppDatabase mDatabase;
    private List<Mark> marks=new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annexe_reserves);
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        int entrepriseID = intent.getIntExtra("entrepriseID",0);
        String nomEntreprise = intent.getStringExtra("nomEntreprise");
        String typeEntreprise = intent.getStringExtra("typeEntreprise");
        String nomChantier = intent.getStringExtra("nomChantier");
        int chantierID = intent.getIntExtra("chantierID",0);


        if(Objects.equals(typeEntreprise, "client")){
            marks=mDatabase.markDao().getAllMarks();


        } else if(Objects.equals(typeEntreprise, "ES")) {
            marks = mDatabase.markDao().getAllMarks();

        }else {
            marks = mDatabase.markDao().getAllMarks();

        }




        MyAdapterReserves adapter = new MyAdapterReserves(this,marks,entrepriseID, nomEntreprise, typeEntreprise);
        recyclerView.setAdapter(adapter);
    }
}


