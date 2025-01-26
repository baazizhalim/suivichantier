package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyBottomSheetDialogFragmentFilter extends BottomSheetDialogFragment {
    protected int planID;
    protected ImageView view = null;
    protected Mark mark = null;
    protected String typeLot;
    protected AppDatabase mDatabase;
    protected List<Mark> marksAffiches = new ArrayList<>();
    protected List<Mark> marksorigines = new ArrayList<>();
    Spinner etat;
    Spinner typeMark;
    Spinner type_Lot;
    Spinner listeMarks;
    EditText dateDebut;
    EditText dateFin;
    Button buttonCancel;
    Button buttonOK;
    Button retour;
    TextView typeLotLabel;




    // Clés pour les arguments
    private static final String ARG_PARAM1 = "plan";
    private static final String ARG_PARAM2 = "typeLot";



    public static MyBottomSheetDialogFragmentFilter newInstance(int param1,  String param2) {
        MyBottomSheetDialogFragmentFilter fragment = new MyBottomSheetDialogFragmentFilter();

        // Transmettre les paramètres via un Bundle
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            planID = getArguments().getInt(ARG_PARAM1);
            typeLot = getArguments().getString(ARG_PARAM2);

            Log.d("typelot", "onCreate: "+typeLot);
        }
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
        retour = fenetre.findViewById(R.id.retour);
        buttonCancel= fenetre.findViewById(R.id.cancel_button);
        typeLotLabel=fenetre.findViewById(R.id.typeLotLabel);


        List<String> choices = new ArrayList<>();
        choices.add("Tout");
        choices.add("reserve");
        choices.add("tache");
        choices.add("note");

// Créer un ArrayAdapter pour le Spinner
        ArrayAdapter<String> adapterFilterMark = new ArrayAdapter<>(requireActivity(),R.layout.spinner_item, choices);
        adapterFilterMark.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeMark.setAdapter(adapterFilterMark);


        List<String> typeLots = new ArrayList<>();
        if(typeLot.equals("CES")) {
            typeLots.add("Tout");
            typeLots.add("Maconnerie");
            typeLots.add("Menuiserie");
            typeLots.add("Plomberie");
            typeLots.add("Enduit");
            typeLots.add("Revetement");
            typeLots.add("Electricite");
            typeLots.add("Equipements");
            typeLots.add("Peinture");
            typeLots.add("Etancheite");
            typeLots.add("Autres");
        }
        else typeLots.add(typeLot);

        ArrayAdapter<String> adapterTypeLot = new ArrayAdapter<>(requireActivity(), R.layout.spinner_item, typeLots);
        adapterTypeLot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_Lot.setAdapter(adapterTypeLot);


        List<String> etatsListe = new ArrayList<>();
        etatsListe.add("Tout");
        etatsListe.add("SNT");
        etatsListe.add("TNV");
        etatsListe.add("TV");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapterEtat = new ArrayAdapter<>(requireActivity(), R.layout.spinner_item, etatsListe);
        adapterEtat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etat.setAdapter(adapterEtat);

        marksorigines.addAll(((Zoom) getActivity()).getAllMarksLocales(planID));

        marksAffiches.addAll(marksorigines);

        ArrayAdapter<Mark> adapterMark = new ArrayAdapter<>(requireActivity(), R.layout.spinner_item, marksAffiches);
        adapterMark.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listeMarks.setAdapter(adapterMark);
        listeMarks.setVisibility(View.VISIBLE);
        typeMark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches.clear();
                                                   marksAffiches.addAll(getListeMarks());
                                                   adapterMark.notifyDataSetChanged();

                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           });
        type_Lot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches.clear();
                                                   marksAffiches.addAll(getListeMarks());
                                                   adapterMark.notifyDataSetChanged();
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           });
        etat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   marksAffiches.clear();
                                                   marksAffiches.addAll(getListeMarks());
                                                   adapterMark.notifyDataSetChanged();
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           });
//        dateDebut.addTextChangedListener(new TextWatcher() {
//            private String current = "";
//            private String yyyymmdd = "YYYYMMDD";
//            private Calendar cal = Calendar.getInstance();
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
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
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                marksAffiches.clear();
//                marksAffiches.addAll(getListeMarks());
//                adapterMark.notifyDataSetChanged();
//            }
//        });


        dateDebut.setFocusable(false);
        dateDebut.setClickable(true);

        dateDebut.setOnClickListener(v -> showDatePickerDialog(dateDebut));
        dateFin.setFocusable(false);
        dateFin.setClickable(true);
        dateFin.setOnClickListener(v -> showDatePickerDialog(dateFin));


//        dateFin.addTextChangedListener(new TextWatcher() {
//            private String current = "";
//            private String yyyymmdd = "YYYYMMDD";
//            private Calendar cal = Calendar.getInstance();
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
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
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                marksAffiches.clear();
//                marksAffiches.addAll(getListeMarks());
//                adapterMark.notifyDataSetChanged();
//            }
//        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                marksAffiches.clear();
//                marksAffiches.addAll(marksorigines);
                ((Zoom) getActivity()).isBottomSheetOpen=false;
                //((Zoom) getActivity()).spinnerPlans.setEnabled(true);

                dismiss(); // Ferme le bottom sheet
            }
        });
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof Zoom) {
                    ((Zoom) getActivity()).isBottomSheetOpen=true;
                    ((Zoom) getActivity()).marksAffiches.clear();
                    ((Zoom) getActivity()).marksAffiches.addAll(marksAffiches);
                    //((Zoom) getActivity()).spinnerPlans.setEnabled(false);
                    ((Zoom) getActivity()).onBottomSheetDismissed(marksAffiches);
                }
                dismiss();

            }
            // Ferme le bottomsheet
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Ferme le bottom sheet
            }
        });

        return fenetre;
    }

//    @Override
//    public void onDismiss(@NonNull DialogInterface dialog) {
//        super.onDismiss(dialog);
//
//
//    }

    private void showDatePickerDialog(EditText date) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    date.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
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
            valeurs[5] = String.valueOf(planID);
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

