package com.example.suivichantier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Statistiques extends AppCompatActivity {

    //private BarChart barChart;
    private AppDatabase mDatabase;
    private int entrepriseID ;
    private String nomEntreprise,typeEntreprise, nomChantier;
    private int  chantierID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        entrepriseID = intent.getIntExtra("entrepriseID",0);
        nomEntreprise = intent.getStringExtra("nomEntreprise");
        typeEntreprise = intent.getStringExtra("typeEntreprise");
        nomChantier = intent.getStringExtra("nomChantier");
        chantierID = intent.getIntExtra("chantierID",0);

        mDatabase = Room.databaseBuilder(this, AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        setContentView(R.layout.bar_char);
        EditText dateDebut=findViewById(R.id.dateDebut);
        EditText dateFin=findViewById(R.id.dateFin);
        Spinner typeMark=findViewById(R.id.typeMark);
        Spinner statut=findViewById(R.id.statut);
        BarChart barChart = findViewById(R.id.barChart);
        Button valider = findViewById(R.id.valider);

        List<String> options1 = new ArrayList<>();
        options1.add("Tout");
        options1.add("SNT");
        options1.add("TNV");
        options1.add("TV");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statut.setAdapter(adapter1);

        List<String> options2 = new ArrayList<>();
        options2.add("Tout");
        options2.add("reserve");
        options2.add("tache");
        options2.add("note");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeMark.setAdapter(adapter2);

valider.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String startDate=dateDebut.getText().toString();if(startDate.isEmpty())startDate="0000-00-00";
        String endDate=dateFin.getText().toString();if(endDate.isEmpty())endDate="5000-00-00";
        String type=typeMark.getSelectedItem().toString();
        String etat=statut.getSelectedItem().toString();

        // Supposons que vous récupérez les données de votre base de données
        List<ConsumptionData> consumptionDataList = getConsumptionDataDuringPeriod(type,etat,chantierID,startDate, endDate);
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < consumptionDataList.size(); i++) {
            barEntries.add(new BarEntry(i, consumptionDataList.get(i).getNbr()));
            labels.add(consumptionDataList.get(i).getType()); // Ajoutez des labels pour chaque barre
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Marques");
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        // Personnalisation optionnelle
        //barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);  // Assurer une légende par barre
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        barChart.animateY(1000);

        barChart.invalidate();

    }
});

    }

    public List<ConsumptionData> getConsumptionDataDuringPeriod(String type, String etat, int ChantierID, String startDate, String endDate) {

        if(!type.equals("Tout") && etat.equals("Tout")) return mDatabase.markDao().getConsumptionBetweenDates2(type,ChantierID,startDate, endDate);
        else if( type.equals("Tout") && etat.equals("Tout") ) return mDatabase.markDao().getConsumptionBetweenDates3(ChantierID,startDate, endDate);
        else if( type.equals("Tout") && !etat.equals("Tout") ) return mDatabase.markDao().getConsumptionBetweenDates4(etat,ChantierID,startDate, endDate);
        else return mDatabase.markDao().getConsumptionBetweenDates1(type,etat,ChantierID,startDate, endDate);

    }

}
