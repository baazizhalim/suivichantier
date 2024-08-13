package com.example.suivichantier;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    protected MarkView markView;
    protected ImageView view=null;
    protected Mark mark=null;
    protected AppDatabase mDatabase;
    protected MotionEvent e;

    TextView title ;
    EditText designation ;
    EditText observation ;
    Spinner etat ;
    Spinner type ;
    EditText dateAjout ;
    Spinner priorite ;
    Zoom z;
    Button buttonCancel;
    Button buttonDelete;
    Button buttonOK;
    Button action_button;
    int statut=0;
    boolean nouveauMark=false;
    private String [] valeurs = new String[6];

    public MyBottomSheetDialogFragment(MarkView markView, MotionEvent e, Zoom z){
        this.markView=markView;
        this.e=e;
        this.z=z;
        if(markView!=null) {
            this.view = markView.getImageView();
            this.mark = markView.getMark();
        }
        else nouveauMark=true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fenetre = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        mDatabase = Room.databaseBuilder(getContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        // Initialisez vos vues ici
         title = fenetre.findViewById(R.id.bottom_sheet_title);
         designation = fenetre.findViewById(R.id.designation);
         observation = fenetre.findViewById(R.id.observation);
         etat = fenetre.findViewById(R.id.etat);
         type = fenetre.findViewById(R.id.type);
         dateAjout = fenetre.findViewById(R.id.dateAjout);
         priorite = fenetre.findViewById(R.id.priorite);

        List<String> options1 = new ArrayList<>();
        options1.add("SNT");
        options1.add("TNV");
        options1.add("TV");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>( this.getContext(), android.R.layout.simple_spinner_item, options1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etat.setAdapter(adapter1);


        List<String> options2 = new ArrayList<>();
        options2.add("reserve");
        options2.add("tache");
        options2.add("note");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>( this.getContext(), android.R.layout.simple_spinner_item, options2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter2);

        List<String> options3 = new ArrayList<>();
        options3.add("Normale");
        options3.add("Urgente");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>( this.getContext(), android.R.layout.simple_spinner_item, options3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorite.setAdapter(adapter3);

        buttonOK = fenetre.findViewById(R.id.bottom_ok_button);
        action_button = fenetre.findViewById(R.id.action_button);
        buttonDelete = fenetre.findViewById(R.id.bottom_delete_button);
        buttonCancel = fenetre.findViewById(R.id.bottom_cancel_button);

        if(mark!=null) {//nouvelle marque
            designation.setText(mark.getDesignation());
            observation.setText(mark.getObservation());
            etat.setSelection(options1.indexOf(mark.getStatut()));
            type.setSelection(options2.indexOf(mark.getType()));
            dateAjout.setText(mark.getDate());
            priorite.setSelection(options3.indexOf(mark.getPriorite()));
            if(etat.getSelectedItem().toString().equals("SNT"))action_button.setBackgroundColor(Color.RED);
            else if (etat.getSelectedItem().toString().equals("TNV"))action_button.setBackgroundColor(Color.BLUE);
            else action_button.setBackgroundColor(Color.GREEN);

        }
        else action_button.setBackgroundColor(Color.RED);


        valeurs=null;


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valeurs=null;
                dismiss(); // Ferme le bottom sheet
            }
        });


        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valeurs=new String[6];
                dismiss(); // Ferme le bottom sheet
            }
        });

        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etat.getSelectedItem().toString().equals("SNT")){
                    action_button.setBackgroundColor(Color.BLUE);
                    etat.setSelection(1);
                }
                else if (etat.getSelectedItem().toString().equals("TNV")){
                    action_button.setBackgroundColor(Color.GREEN);
                    etat.setSelection(2);
                }

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                z.layout.removeView(view);
                z.marks.remove(mark);
                z.markViews.remove(markView);
                mDatabase.markDao().delete(mark);
                valeurs=null;
                dismiss(); // Ferme le bottom sheet
            }
        });

        return fenetre;
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
        layoutParams.height = getScreenHeight() * 3 / 4; // Par exemple, définir à 75% de la hauteur de l'écran
        bottomSheet.setLayoutParams(layoutParams);


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(valeurs!=null) {
            valeurs = getValeurs(); // Récupérer les valeurs depuis le bottomSheet
            if (nouveauMark) {
                z.createMark(e, valeurs);
                nouveauMark = false;
            } else {
                mark.setDesignation(designation.getText().toString());
                mark.setObservation(observation.getText().toString());
                mark.setStatut(etat.getSelectedItem().toString());
                mark.setType(type.getSelectedItem().toString());
                mark.setDate(dateAjout.getText().toString());
                mark.setPriorite(priorite.getSelectedItem().toString());
                if(etat.getSelectedItem().toString().equals("SNT"))view.setBackgroundColor(Color.RED);
                else if (etat.getSelectedItem().toString().equals("TNV"))view.setBackgroundColor(Color.BLUE);
                else view.setBackgroundColor(Color.GREEN);
                mDatabase.markDao().update(mark);
            }
        }
    }

    private int getScreenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

       public String[] getValeurs() {
        if(valeurs!=null){
        valeurs[0]=designation.getText().toString();
        valeurs[1]=observation.getText().toString();
        valeurs[2]=etat.getSelectedItem().toString();;
        valeurs[3]=type.getSelectedItem().toString();
        valeurs[4]=dateAjout.getText().toString();
        valeurs[5]=priorite.getSelectedItem().toString();
        }
        return valeurs;
    }
}

