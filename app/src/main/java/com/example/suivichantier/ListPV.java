package com.example.suivichantier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ListPV extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_pv);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4", "Item 5");
        MyAdapterPV adapter = new MyAdapterPV(items);
        recyclerView.setAdapter(adapter);
    }
}
