package com.example.suivichantier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyBottomSheetDialogFragmentFilter extends BottomSheetDialogFragment {
    protected Plan plan;
    protected MarkView markView;
    protected ImageView view = null;
    protected Mark mark = null;
    protected int entrepriseID;
    protected String typeEntreprise;
    protected String typeLot;
    protected AppDatabase mDatabase;
    protected   List<Mark> marksAffiches = new ArrayList<>();
    Spinner etat;
    Spinner typeMark;
    Spinner type_Lot;
    Spinner listeMarks;
    EditText dateDebut;
    EditText dateFin;
    Zoom z;
    Button buttonCancel;
    Button buttonOK;

    int statut = 0;

    private OkHttpClient client;



    public MyBottomSheetDialogFragmentFilter(Plan  plan,  Zoom z, int entrepriseID, String typeEntreprise, String typelot) {
        this.plan = plan;
        this.z = z;
        this.entrepriseID = entrepriseID;
        this.typeEntreprise = typeEntreprise;
        this.typeLot = typelot;

    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fenetre = inflater.inflate(R.layout.bottom_sheet_layout_filter, container, false);
        mDatabase = Room.databaseBuilder(getContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        client = new OkHttpClient();

        typeMark = fenetre.findViewById(R.id.typeMark);
        type_Lot = fenetre.findViewById(R.id.typeLot);
        etat = fenetre.findViewById(R.id.etat);
        listeMarks = fenetre.findViewById(R.id.listeMarks);
        dateDebut=fenetre.findViewById(R.id.dateDebut);
        dateFin=fenetre.findViewById(R.id.dateFin);
        buttonOK= fenetre.findViewById(R.id.ok_button);
        buttonCancel= fenetre.findViewById(R.id.cancel_button);



        // Initialisez vos vues ici


        List<String> choices = new ArrayList<>();
        choices.add("Tout type");
        choices.add("Reserve");
        choices.add("Tache");
        choices.add("Note");

// Créer un ArrayAdapter pour le Spinner
        ArrayAdapter<String> adapterFilterMark = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, choices);
        adapterFilterMark.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeMark.setAdapter(adapterFilterMark);


        List<String> typeLots = new ArrayList<>();
        typeLots.add("S/O");
        typeLots.add("Maçonnerie");
        typeLots.add("Ménuiserie");
        typeLots.add("Plomberie");
        typeLots.add("enduit");
        typeLots.add("revetement");
        typeLots.add("electricite");
        typeLots.add("Equipements");
        typeLots.add("Peinture");
        typeLots.add("Etancheite");
        typeLots.add("Autres");

        ArrayAdapter<String> adapterTypeLot = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, typeLots);
        adapterTypeLot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_Lot.setAdapter(adapterTypeLot);


        List<String> etatsListe = new ArrayList<>();
        etatsListe.add("SNT");
        etatsListe.add("TNV");
        etatsListe.add("TV");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapterEtat = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, etatsListe);
        adapterEtat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etat.setAdapter(adapterEtat);
        typeMark.setSelection(0);
        type_Lot.setVisibility(View.INVISIBLE);
        if(typeLot.equals("CES")){
            type_Lot.setVisibility(View.VISIBLE);
            type_Lot.setSelection(0);
            marksAffiches = z.getAllMarks(plan);
            ArrayAdapter<Mark> adapterMark = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, marksAffiches);
            adapterMark.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listeMarks.setAdapter(adapterMark);
            listeMarks.setVisibility(View.VISIBLE);

        }
        typeMark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches = getListeMarks();
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           }
        );
        type_Lot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches = getListeMarks();
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           }
        );

        etat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches = getListeMarks();
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           }
        );

        dateDebut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                marksAffiches = getListeMarks();
            }
        });

        dateFin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                marksAffiches = getListeMarks();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Ferme le bottom sheet
            }
        });


        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Ferme le bottom sheet
            }
        });

        return fenetre;
    }







    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

             // Récupérer les valeurs depuis le bottomSheet

            z.afficherMarkFiltres(marksAffiches);

    }

    private int getScreenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    public List<Mark> getListeMarks() {
        List<Mark> marks=new ArrayList<>();
        String[] valeurs = new String[6];
            valeurs[0] = typeMark.getSelectedItem().toString();
            valeurs[1] = type_Lot.getSelectedItem().toString();
            valeurs[2] = etat.getSelectedItem().toString();
            valeurs[3] = dateDebut.getText().toString();
            valeurs[4] = dateFin.getText().toString();
            valeurs[5] = String.valueOf(plan.getPlanID());
            marks=calculer(valeurs);
            return marks;
    }

    private List<Mark> calculer(String[] valeurs) {
        List<Mark> marks;
        marks = mDatabase.markDao().getMarkscriteres(valeurs[0],valeurs[1],valeurs[2],valeurs[3],valeurs[4],valeurs[5]);
        return marks;
    }


}

