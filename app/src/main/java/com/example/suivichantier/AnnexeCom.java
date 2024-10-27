package com.example.suivichantier;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnexeCom extends AppCompatActivity {


    private AppDatabase mDatabase;
    private List<Communique> com=new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_com);
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        int chantierID = intent.getIntExtra("chantierID",0);
        com=mDatabase.communiqueDao().getAllCommuniques(chantierID);

        MyAdapterCom adapter = new MyAdapterCom(this,com);
        recyclerView.setAdapter(adapter);
    }
}



