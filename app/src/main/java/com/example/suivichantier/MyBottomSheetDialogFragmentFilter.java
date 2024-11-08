package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.room.Room;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

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
    protected   List<Mark> marksorigines = new ArrayList<>();
    Spinner etat;
    Spinner typeMark;
    Spinner type_Lot;
    Spinner listeMarks;
    EditText dateDebut;
    EditText dateFin;
    Zoom z;
    Button buttonCancel;
    Button buttonOK;
    TextView typeLotLabel;


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
        mDatabase = Room.databaseBuilder(requireActivity(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        typeMark = fenetre.findViewById(R.id.typeMark);
        type_Lot = fenetre.findViewById(R.id.typeLot);
        etat = fenetre.findViewById(R.id.etat);
        listeMarks = fenetre.findViewById(R.id.listeMarks);
        dateDebut=fenetre.findViewById(R.id.dateDebut);
        dateFin=fenetre.findViewById(R.id.dateFin);
        buttonOK= fenetre.findViewById(R.id.ok_button);
        buttonCancel= fenetre.findViewById(R.id.cancel_button);
        typeLotLabel=fenetre.findViewById(R.id.typeLotLabel);



        // Initialisez vos vues ici


        List<String> choices = new ArrayList<>();
        choices.add("Tout");
        choices.add("reserve");
        choices.add("tache");
        choices.add("note");

// Créer un ArrayAdapter pour le Spinner
        ArrayAdapter<String> adapterFilterMark = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, choices);
        adapterFilterMark.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeMark.setAdapter(adapterFilterMark);


        List<String> typeLots = new ArrayList<>();
        if(typeLot.equals("CES")) {
            typeLots.add("Tout");
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
        }
        else typeLots.add(typeLot);

        ArrayAdapter<String> adapterTypeLot = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, typeLots);
        adapterTypeLot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_Lot.setAdapter(adapterTypeLot);


        List<String> etatsListe = new ArrayList<>();
        etatsListe.add("Tout");
        etatsListe.add("SNT");
        etatsListe.add("TNV");
        etatsListe.add("TV");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapterEtat = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, etatsListe);
        adapterEtat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etat.setAdapter(adapterEtat);

        marksorigines.addAll(z.getAllMarks(plan));
        marksAffiches.addAll(marksorigines);
        ArrayAdapter<Mark> adapterMark = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, marksAffiches);
        adapterMark.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listeMarks.setAdapter(adapterMark);
        listeMarks.setVisibility(View.VISIBLE);

        typeMark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches.clear();
                                                   marksAffiches.addAll(getListeMarks());


                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           }
        );
        type_Lot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches.clear();
                                                   marksAffiches.addAll(getListeMarks());
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           }
        );

        etat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches.clear();
                                                   marksAffiches.addAll(getListeMarks());
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
                marksAffiches.clear();
                marksAffiches.addAll(getListeMarks());
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
                marksAffiches.clear();
                marksAffiches.addAll(getListeMarks());
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marksAffiches.addAll(marksorigines);
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

    public void onStart() {
        super.onStart();

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        dialog.setCanceledOnTouchOutside(false);

        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);


        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Pour définir la hauteur maximale
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = getScreenHeight(); // Par exemple, définir à 75% de la hauteur de l'écran
        bottomSheet.setLayoutParams(layoutParams);

    }

    private int getScreenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    public List<Mark> getListeMarks() {

        String[] valeurs = new String[6];
            valeurs[0] = typeMark.getSelectedItem().toString();
            valeurs[1] = type_Lot.getSelectedItem().toString();
            valeurs[2] = etat.getSelectedItem().toString();
            valeurs[3] = dateDebut.getText().toString();if(valeurs[3].isEmpty())valeurs[3]="0000-00-00";
            valeurs[4] = dateFin.getText().toString();if(valeurs[4].isEmpty())valeurs[4]="5000-00-00";
            valeurs[5] = String.valueOf(plan.getPlanID());
        return calculer(valeurs[0],valeurs[1],valeurs[2],valeurs[3],valeurs[4],valeurs[5]);

    }

    private List<Mark> calculer(String typeMark,String typeLot,String etat,String dateDebut,String dateFin,String planID) {

        if(typeMark.equals("Tout") && etat.equals("Tout") && !typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres1(  typeLot,  dateDebut,  dateFin,  planID);

        else if(typeMark.equals("Tout") && !etat.equals("Tout")&& !typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres2( etat, typeLot,   dateDebut,  dateFin,  planID);

        else if(!typeMark.equals("Tout") && etat.equals("Tout") && !typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres3( typeMark,  typeLot,   dateDebut,  dateFin,  planID);

        else if(!typeMark.equals("Tout") && !etat.equals("Tout") && !typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres4( typeMark,   etat, typeLot, dateDebut,  dateFin,  planID);

        else if(typeMark.equals("Tout") && etat.equals("Tout") && typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres5(  dateDebut,  dateFin,  planID);

        if(!typeMark.equals("Tout") && etat.equals("Tout") && typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres6(  typeMark,  dateDebut,  dateFin,  planID);

        else if(typeMark.equals("Tout") && !etat.equals("Tout")&& typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres7( etat,  dateDebut,  dateFin,  planID);

        else if(!typeMark.equals("Tout") && !etat.equals("Tout") && typeLot.equals("Tout"))
            return mDatabase.markDao().getMarkscriteres8( typeMark,  etat,   dateDebut,  dateFin,  planID);

        return null;
    }


}

