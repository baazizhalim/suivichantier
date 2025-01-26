package com.example.suivichantier;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.room.Room;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Statistiques extends AppCompatActivity {

    //private BarChart barChart;
    private AppDatabase mDatabase;
    private int entrepriseID ;
    private String nomEntreprise,typeEntreprise, nomChantier;
    private int  chantierID;
    private EditText dateDebut;
    private EditText dateFin;
    private Spinner typeMark;
    private Spinner statut;
    private Spinner lot;
    private BarChart barChart ;


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

        setContentView(R.layout.statistiques);

        dateDebut = findViewById(R.id.dateDebut);
        dateDebut.setFocusable(false);
        dateDebut.setClickable(true);
        dateDebut.setOnClickListener(v -> showDatePickerDialog(dateDebut));

        dateFin=findViewById(R.id.dateFin);
        dateFin.setFocusable(false);
        dateFin.setClickable(true);
        dateFin.setOnClickListener(v -> showDatePickerDialog(dateFin));


//        dateDebut.addTextChangedListener(new TextWatcher() {
//            private String current = "";
//            private String yyyymmdd = "YYYYMMDD";
//            private Calendar cal = Calendar.getInstance();
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!s.toString().equals(current)) {
//                    String clean = s.toString().replaceAll("[^\\d.]", "");
//                    String cleanC = current.replaceAll("[^\\d.]", "");
//
//                    int cl = clean.length();
//                    int sel = cl;
//                    for (int i = 2; i <= cl && i < 6; i += 2) {
//                        sel++;
//                    }
//                    // Fix for pressing delete next to a forward slash
//                    if (clean.equals(cleanC)) sel--;
//
//                    if (clean.length() < 8) {
//                        clean = clean + yyyymmdd.substring(clean.length());
//                    } else {
//                        // This part makes sure that when we finish entering numbers
//                        // the date is correct, fixing it otherwise
//                        int year = Integer.parseInt(clean.substring(0, 4));
//                        int mon = Integer.parseInt(clean.substring(4, 6));
//                        int day = Integer.parseInt(clean.substring(6, 8));
//
//                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
//                        cal.set(Calendar.MONTH, mon - 1);
//                        year = year < 1900 ? 1900 : year > 2100 ? 2100 : year;
//                        cal.set(Calendar.YEAR, year);
//                        // ^ first set year for the line below to work correctly
//                        // with leap years - otherwise, date e.g. 29/02/2012
//                        // would be automatically corrected to 28/02/2012
//
//                        day = day > cal.getActualMaximum(Calendar.DATE) ? cal.getActualMaximum(Calendar.DATE) : day;
//                        clean = String.format("%04d%02d%02d", year, mon, day);
//                    }
//
//                    clean = String.format("%s-%s-%s", clean.substring(0, 4),
//                            clean.substring(4, 6),
//                            clean.substring(6, 8));
//
//                    sel = sel < 0 ? 0 : sel;
//                    current = clean;
//                    dateDebut.setText(current);
//                    dateDebut.setSelection(sel < current.length() ? sel : current.length());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });



//        dateFin.addTextChangedListener(new TextWatcher() {
//            private String current = "";
//            private String yyyymmdd = "YYYYMMDD";
//            private Calendar cal = Calendar.getInstance();
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!s.toString().equals(current)) {
//                    String clean = s.toString().replaceAll("[^\\d.]", "");
//                    String cleanC = current.replaceAll("[^\\d.]", "");
//
//                    int cl = clean.length();
//                    int sel = cl;
//                    for (int i = 2; i <= cl && i < 6; i += 2) {
//                        sel++;
//                    }
//                    // Fix for pressing delete next to a forward slash
//                    if (clean.equals(cleanC)) sel--;
//
//                    if (clean.length() < 8) {
//                        clean = clean + yyyymmdd.substring(clean.length());
//                    } else {
//                        // This part makes sure that when we finish entering numbers
//                        // the date is correct, fixing it otherwise
//                        int year = Integer.parseInt(clean.substring(0, 4));
//                        int mon = Integer.parseInt(clean.substring(4, 6));
//                        int day = Integer.parseInt(clean.substring(6, 8));
//
//                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
//                        cal.set(Calendar.MONTH, mon - 1);
//                        year = year < 1900 ? 1900 : year > 2100 ? 2100 : year;
//                        cal.set(Calendar.YEAR, year);
//                        // ^ first set year for the line below to work correctly
//                        // with leap years - otherwise, date e.g. 29/02/2012
//                        // would be automatically corrected to 28/02/2012
//
//                        day = day > cal.getActualMaximum(Calendar.DATE) ? cal.getActualMaximum(Calendar.DATE) : day;
//                        clean = String.format("%04d%02d%02d", year, mon, day);
//                    }
//
//                    clean = String.format("%s-%s-%s", clean.substring(0, 4),
//                            clean.substring(4, 6),
//                            clean.substring(6, 8));
//
//                    sel = sel < 0 ? 0 : sel;
//                    current = clean;
//                    dateFin.setText(current);
//                    dateFin.setSelection(sel < current.length() ? sel : current.length());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });

        typeMark=findViewById(R.id.typeMark);
         statut=findViewById(R.id.statut);
         lot=findViewById(R.id.lot);
         barChart = findViewById(R.id.barChart);
        //Button valider = findViewById(R.id.valider);

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

        List<String> options3 = new ArrayList<>();
        options3.add("Tout");
        options3.add("Maconnerie");
        options3.add("Menuiserie");
        options3.add("Plomberie");
        options3.add("Enduit");
        options3.add("Revetement");
        options3.add("Electricite");
        options3.add("Equipements");
        options3.add("Peinture");
        options3.add("Etancheite");
        options3.add("Autres");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lot.setAdapter(adapter3);



    }

    private void showDatePickerDialog(EditText date) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    date.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
    public List<ConsumptionData> getConsumptionDataDuringPeriod(String type, String etat, int ChantierID, String startDate, String endDate) {

        if(!type.equals("Tout") && etat.equals("Tout")) return mDatabase.markDao().getConsumptionBetweenDates2(type,ChantierID,startDate, endDate);
        else if( type.equals("Tout") && etat.equals("Tout") ) return mDatabase.markDao().getConsumptionBetweenDates3(ChantierID,startDate, endDate);
        else if( type.equals("Tout") && !etat.equals("Tout") ) return mDatabase.markDao().getConsumptionBetweenDates4(etat,ChantierID,startDate, endDate);
        else return mDatabase.markDao().getConsumptionBetweenDates1(type,etat,ChantierID,startDate, endDate);

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


   private void  valider() {
            String startDate=dateDebut.getText().toString();if(startDate.isEmpty())startDate="0000-00-00";
            String endDate=dateFin.getText().toString();if(endDate.isEmpty())endDate="5000-00-00";
            String type=typeMark.getSelectedItem().toString();
            String etat=statut.getSelectedItem().toString();

            // Supposons que vous récupérez les données de votre base de données
            List<ConsumptionData> consumptionDataList = getConsumptionDataDuringPeriod(type,etat,chantierID,startDate, endDate);
            Map <String,Integer> listLot=new HashMap<>();
            consumptionDataList.forEach(consumptionData -> listLot.put(consumptionData.getType().toUpperCase(),consumptionData.getNbr()));

            List<BarEntry> barEntries = new ArrayList<>();
            String[] labels =  {"Infra","Super","Vrd","Maconnerie","Menuiserie","Plomberie","Enduit","Revetement","Electricite","Equipements","Peinture","Etancheite","Autres"};



            for (int i = 0; i < labels.length; i++) {
                int valeur=0;
                //int valeur=mDatabase.markDao().nbrMarkByLot(chantierID,labels[i]);
                //Log.d("valeurs", "onClick:  "+labels[i]+":"+ valeur);
                if(listLot.containsKey(labels[i].toUpperCase()))valeur=listLot.get(labels[i].toUpperCase());
                barEntries.add(new BarEntry(i, valeur));

            }

            int[] colors =  {Color.GREEN,Color.CYAN,Color.YELLOW,Color.BLUE,Color.GRAY,Color.RED,Color.MAGENTA,Color.BLACK,Color.LTGRAY,Color.YELLOW,Color.GREEN,Color.RED,Color.CYAN};
            BarDataSet barDataSet = new BarDataSet(barEntries, "");
            barDataSet.setColors(colors);
            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.2f);
            barChart.setData(barData);
            barChart.getXAxis().setGranularity(1f); // Force l'affichage d'un label par position
            barChart.getXAxis().setGranularityEnabled(false);
            barChart.getXAxis().setLabelCount(13, true);
            barChart.getDescription().setText("LOTS"); // Exemple de description
            barChart.getDescription().setTextSize(12f); // Taille du texte
            barChart.getDescription().setTextColor(Color.RED); // Couleur du texte
            barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {

                    return labels[(int) value];
                }
            });
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            barChart.getXAxis().setDrawLabels(true);
            barChart.getXAxis().setLabelRotationAngle(80);
            barChart.animateY(1000);
            barChart.setFitBars(true);
            barChart.getLegend().setEnabled(false);
            barChart.invalidate();


            PieChart pieChart = findViewById(R.id.pieChart);
            ArrayList<PieEntry> entries = new ArrayList<>();
            int nbr_mark=mDatabase.markDao().nbrMark(chantierID);
            int nbr_mark_signalee=mDatabase.markDao().nbrMarkSignalee(chantierID);
            int nbr_mark_Traitee_non_verifee=mDatabase.markDao().nbrMarkTraiteeNonVerifee(chantierID);
            int nbr_mark_Traitee_et_verifee=mDatabase.markDao().nbrMarkTraiteeEtVerifee(chantierID);
            Log.d("nbr_mark", "onClick: "+nbr_mark);
            Log.d("nbr_mark_signalee", "onClick: "+nbr_mark_signalee);
            Log.d("nbr_mark_Traitee_non_verifee", "onClick: "+nbr_mark_Traitee_non_verifee);
            Log.d("nbr_mark_Traitee_et_verifee", "onClick: "+nbr_mark_Traitee_et_verifee);
            entries.add(new PieEntry((float) nbr_mark_signalee / nbr_mark, "Signalée"));
            entries.add(new PieEntry((float) nbr_mark_Traitee_non_verifee /nbr_mark, "Traitée non vérifiée"));
            entries.add(new PieEntry((float) nbr_mark_Traitee_et_verifee /nbr_mark, "Traitée et vérifiée"));

            // Créer un DataSet
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN); // Couleurs des parts
            dataSet.setValueTextColor(Color.WHITE); // Couleur des valeurs
            dataSet.setValueTextSize(10f); // Taille du texte des valeurs
       dataSet.setValueFormatter(new ValueFormatter() {
           @Override
           public String getFormattedValue(float value) {
               // Créer un formateur pour deux chiffres après la virgule
               DecimalFormat decimalFormat = new DecimalFormat("0.00");
               return decimalFormat.format(value) + "%"; // Ajouter le symbole % après la valeur formatée
           }
       });
            // Créer les données du PieChart
            PieData pieData = new PieData(dataSet);
            pieData.setValueTextColor(Color.WHITE);
            pieData.setValueTextSize(12f); // Taille en SP
            pieData.setValueTypeface(Typeface.DEFAULT_BOLD);
            pieChart.setData(pieData);
            pieChart.setCenterText("Statistiques"); // Texte au centre
            pieChart.setCenterTextSize(14f);
            pieChart.setDrawEntryLabels(false);
            pieChart.setUsePercentValues(true); // Afficher les pourcentages
            pieChart.getDescription().setEnabled(false); // Désactiver la description
            pieChart.animateY(1400); // Animation

            // Rafraîchir le graphique
            pieChart.invalidate();

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_3, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.OK) {
            valider();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }



}
