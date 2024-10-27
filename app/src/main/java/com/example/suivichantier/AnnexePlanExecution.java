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

public class AnnexePlanExecution extends AppCompatActivity {


        private AppDatabase mDatabase;
        private List<PlanExecution> pe=new ArrayList<>();
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_planexecution);
            mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            Intent intent = getIntent();
            int chantierID = intent.getIntExtra("chantierID",0);
            pe=mDatabase.planExecutionDao().getAllPlanExecutions(chantierID);

            MyAdapterPlanExecution adapter = new MyAdapterPlanExecution(this,pe);
            recyclerView.setAdapter(adapter);
        }
    }



