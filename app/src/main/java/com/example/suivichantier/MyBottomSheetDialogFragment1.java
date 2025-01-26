package com.example.suivichantier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyBottomSheetDialogFragment1 extends BottomSheetDialogFragment {
    protected MarkView markView;
    protected ImageView view = null;
    protected Mark mark = null;
    protected int entrepriseID;
    protected String typeEntreprise;
    protected String nomEntreprise;
    protected String nomChantier;
    protected String typeLot;
    protected AppDatabase mDatabase;
    protected MotionEvent e;
    TextView title;
    EditText designation;
    EditText observation;
    Spinner etat;
    Spinner type;
    Spinner lot;
    //EditText dateAjout;
    Spinner priorite;
    Zoom1 z;
    Button buttonCancel;
    Button buttonDelete;
    Button buttonOK;
    Button retour;
    Button buttonModifier;
    //Button buttonSynchroMark;
    Button btnCapture;
    private float lastTouchX;
    private float lastTouchY;
    int statut = 0;
    boolean nouveauMark = false;
    private String[] valeurs = new String[7];
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Bitmap capturedImage;
    protected LinearLayout barePhoto;
    protected TableRow[] rows = new TableRow[2];
    protected int i = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private final Matrix matrix = new Matrix();


    public MyBottomSheetDialogFragment1(MarkView markView, MotionEvent e, Zoom1 z, int entrepriseID, String nomEntreprise, String typeEntreprise, String NomChantier, String typelot) {
        this.markView = markView;
        this.e = e;
        this.z = z;
        this.entrepriseID = entrepriseID;
        this.nomEntreprise = nomEntreprise;
        this.nomChantier = NomChantier;
        this.typeEntreprise = typeEntreprise;
        this.typeLot = typelot;
        if (markView != null) {
            this.view = markView.getImageButton();
            this.mark = markView.getMark();
        } else nouveauMark = true;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fenetre = inflater.inflate(R.layout.bottom_sheet_layout1, container, false);
        mDatabase = Room.databaseBuilder(getContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        rows[0] = fenetre.findViewById(R.id.row1);
        rows[1] = fenetre.findViewById(R.id.row2);


        // Initialisez vos vues ici
        title = fenetre.findViewById(R.id.bottom_sheet_title);
        designation = fenetre.findViewById(R.id.designation);
        observation = fenetre.findViewById(R.id.observation);
        etat = fenetre.findViewById(R.id.etat);
        type = fenetre.findViewById(R.id.type);
        lot = fenetre.findViewById(R.id.lot);
        //dateAjout = fenetre.findViewById(R.id.dateAjout);
        priorite = fenetre.findViewById(R.id.priorite);

        List<String> options1 = new ArrayList<>();
        options1.add("SNT");
        options1.add("TNV");
        options1.add("TV");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this.getContext(), R.layout.spinner_item, options1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etat.setAdapter(adapter1);


        List<String> options2 = new ArrayList<>();
        options2.add("reserve");
        options2.add("tache");
        options2.add("note");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this.getContext(), R.layout.spinner_item, options2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter2);

        List<String> lots = new ArrayList<>();
        if (typeLot.equals("CES")) {
            lots.add("Maconnerie");
            lots.add("Menuiserie");
            lots.add("Plomberie");
            lots.add("enduit");
            lots.add("revetement");
            lots.add("electricite");
            lots.add("Equipements");
            lots.add("Peinture");
            lots.add("Etancheite");
            lots.add("Autres");
        } else lots.add(typeLot);

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this.getContext(), R.layout.spinner_item, lots);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lot.setAdapter(adapter4);


        List<String> options3 = new ArrayList<>();
        options3.add("Normale");
        options3.add("Urgente");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this.getContext(), R.layout.spinner_item, options3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorite.setAdapter(adapter3);

        buttonOK = fenetre.findViewById(R.id.bottom_ok_button);
        retour = fenetre.findViewById(R.id.retour);
        buttonModifier = fenetre.findViewById(R.id.bottom_modifier_button);
        buttonDelete = fenetre.findViewById(R.id.bottom_delete_button);
        if (typeEntreprise.equals("ER")) buttonDelete.setEnabled(false);
        buttonCancel = fenetre.findViewById(R.id.bottom_cancel_button);
        btnCapture = fenetre.findViewById(R.id.btnCapture);
        btnCapture.setEnabled(false);

        if (mark != null) {
            if (!typeEntreprise.equals("ER") && getNumeroPhotoDisponible(mark.getMarkID()) != 0) btnCapture.setEnabled(true);
            designation.setText(mark.getDesignation());
            observation.setText(mark.getObservation());
            etat.setSelection(options1.indexOf(mark.getStatut()));
            type.setSelection(options2.indexOf(mark.getType()));
            lot.setSelection(lots.indexOf(mark.getLot()));
            priorite.setSelection(options3.indexOf(mark.getPriorite()));
            if (etat.getSelectedItem().toString().equals("SNT"))
                buttonModifier.setBackgroundColor(darkenColor(Color.RED, 0.5f));
            else if (etat.getSelectedItem().toString().equals("TNV"))
                buttonModifier.setBackgroundColor(darkenColor(Color.BLUE, 0.5f));
            else buttonModifier.setBackgroundColor(darkenColor(Color.GREEN, 0.5f));
            afficherBoutonPhotos(mark.getMarkID());
        } else {
            btnCapture.setEnabled(false);
            //buttonSynchroMark.setEnabled(false);
            buttonModifier.setBackgroundColor(darkenColor(Color.RED, 0.5f));

        }


        valeurs = null;


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valeurs = null;
                dismiss(); // Ferme le bottom sheet
            }
        });


        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valeurs = new String[7];
                dismiss(); // Ferme le bottom sheet
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Ferme le bottom sheet
            }
        });

        buttonModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etat.getSelectedItem().toString().equals("SNT") && mark != null) {
                    buttonModifier.setBackgroundColor(darkenColor(Color.BLUE, 0.5f));
                    etat.setSelection(1);
                } else if (etat.getSelectedItem().toString().equals("TNV")) {
                    buttonModifier.setBackgroundColor(darkenColor(Color.GREEN, 0.5f));
                    etat.setSelection(2);
                }

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                supprimerMark();
            }
        });


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    openCamera();
                }

            }
        });

        return fenetre;
    }

    public int darkenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a, r, g, b);
    }

    private void afficherBoutonPhotos(String markID) {
        rows[0].removeAllViews();
        rows[1].removeAllViews();
        List<Photo> photos = mDatabase.photoDao().getAllPhotosMark(markID);
        Button[] photoButtons = new Button[2];
        Button[] photoDeletes = new Button[2];

        final int[] i = {0};
        photos.forEach((Photo photo) -> {
            int index = i[0];
            photoButtons[index] = new Button(getContext());
            photoButtons[index].setText(photo.getFile());
            photoButtons[index].setContentDescription(String.valueOf(photo.getPhotoID()));
            rows[index].addView(photoButtons[index]);
            photoDeletes[index] = new Button(getContext());
            photoDeletes[index].setText("Delete");
            photoDeletes[index].setContentDescription(String.valueOf(photo.getPhotoID()));
            if (typeEntreprise.equals("ER")) photoDeletes[index].setEnabled(false);
            rows[index].addView(photoDeletes[index]);
            photoButtons[index].setOnClickListener(new View.OnClickListener() {

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onClick(View v) {


                    File appDir = getContext().getFilesDir(); // Répertoire principal de l'application
                    File myDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + "photos/" + mark.getMarkID()); // Sous-répertoire
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    //Photo photo = mDatabase.photoDao().getOnePhoto(Integer.parseInt(v.getContentDescription().toString()));
                    String imagePath = myDir + "/" + photo.getFile();

                    // Charger l'image à partir du fichier JPEG
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);

                    // Créez une boîte de dialogue pour afficher l'image
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.affiche_image);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    // Trouvez l'ImageView dans le layout de la boîte de dialogue
                    ImageView imageView = dialog.findViewById(R.id.imageView);

                    //imageView.setImageMatrix(matrix);
                    //ImageButton zoomInButton = dialog.findViewById(R.id.zoom_in_button);
                    //ImageButton zoomOutButton = dialog.findViewById(R.id.zoom_out_button);
                    ImageButton pen = dialog.findViewById(R.id.pen);
                    ImageButton close = dialog.findViewById(R.id.close);
                    // Affichez l'image chargée dans l'ImageView
                    imageView.setImageBitmap(imageBitmap);
                    scaleGestureDetector = new ScaleGestureDetector(requireContext(), new MyBottomSheetDialogFragment1.ScaleListener(imageView, imageBitmap));


                    imageView.setOnTouchListener((vv, event) -> {
                        scaleGestureDetector.onTouchEvent(event);
                        final int action = event.getAction();
                        switch (action & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN: {
                                final float x = event.getX();
                                final float y = event.getY();
                                lastTouchX = x;
                                lastTouchY = y;
                                break;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                final float x = event.getX();
                                final float y = event.getY();
                                final float dx = x - lastTouchX;
                                final float dy = y - lastTouchY;
                                matrix.postTranslate(dx, dy);
                                imageView.setImageMatrix(matrix);

                                lastTouchX = x;
                                lastTouchY = y;


                                break;
                            }


                            default:
                                return false;
                        }
                        return true;
                    });

                    pen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UpdateImage updateimage = new UpdateImage(getContext(), imageBitmap, imagePath);
                            updateimage.show();
                            updateimage.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                                    imageView.setImageBitmap(imageBitmap);
                                }
                            });

                        }
                    });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();


                        }
                    });


                    dialog.show();
                }
            });
            photoDeletes[index].setOnClickListener(v -> {
                attentionSupPhoto(photo, index);


            });
            i[0]++;
        });


    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        ImageView imageView;
        Bitmap bitmap;

        public ScaleListener(ImageView imageView, Bitmap bitmap) {
            this.imageView = imageView;
            this.bitmap = bitmap;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            float dx = (float) imageView.getWidth() / 2 - (float) bitmap.getWidth() / 2 * scaleFactor;
            float dy = (float) imageView.getHeight() / 2 - (float) bitmap.getHeight() / 2 * scaleFactor;
            matrix.postTranslate(dx, dy);
            imageView.setImageMatrix(matrix);
            return false;
        }
    }

    private void attentionSupPhoto(Photo photo, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Suppression Photo");
        builder.setMessage("la Photo va être supprimée ?");

        // Bouton Oui
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(requireContext(), "Action confirmée", Toast.LENGTH_SHORT).show();
                deleteLocalFile(photo.getFile());
                rows[index].removeAllViews();
                btnCapture.setEnabled(true);
            }
        });

        // Bouton Non
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(requireContext(), "Action annulée", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Fermer le dialogue
            }
        });

        // Créer et afficher le dialogue
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteLocalFile(String fileName) {
        File appDir = getContext().getFilesDir();
        File file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + mark.getMarkID() + "/" + fileName);
        if (file.exists()) {
            file.delete();
            deleteLocalyFilefromDB(fileName);
        }
        file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + mark.getMarkID());
        if (file.exists() && file.isDirectory() && file.listFiles().length == 0) file.delete();
    }

    private void deleteLocalFileByMark(Mark mark) {
        List<String> files = getLocalFiles(mark.getMarkID());
        File appDir = getContext().getFilesDir();
        files.forEach(fileName -> {
            File file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + mark.getMarkID() + "/" + fileName);
            if (file.exists()) {
                file.delete();
                deleteLocalyFilefromDB(fileName);

            }
        });
        File file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + mark.getMarkID());
        if (file.exists() && file.isDirectory() && file.listFiles().length == 0) file.delete();
    }

    private void deleteLocalyFilefromDB(String fileName) {
        mDatabase.photoDao().deletePhoto(fileName);

    }

    private List<String> getLocalFiles(String markID) {
        File appDir = getContext().getFilesDir();
        List<String> fileNames = new ArrayList<>();
        File directory = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + markID + "/");
        if (directory.list() != null) fileNames = Arrays.asList(directory.list());
        return fileNames;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, "large"); // ou une autre résolution spécifique

        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");
            saveImage(capturedImage);
        }
    }


    public int getNumeroPhotoDisponible(String markID) {
        int num = 0;
        List<Photo> photos = new ArrayList<>();
        photos = mDatabase.photoDao().getAllPhotosMark(markID);
        if (photos.size() == 0) num = 1;
        else if (photos.size() == 2) num = 0;
        else {
            String id = photos.get(0).getPhotoID();
            String[] parts = id.split("_");
            // Récupérer la dernière partie, qui est la valeur de x
            String xString = parts[parts.length - 1];
            // Convertir cette valeur en entier
            num = Integer.parseInt(xString);
            num = num % 2 + 1;
        }

        return num;
    }

    private void saveImage(Bitmap bitmap) {
        LocalDate currentDate = LocalDate.now();

        // Formater la date au format YYYY-MM-DD
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        int i = getNumeroPhotoDisponible(mark.getMarkID());
        String fileName = "photo_" + mark.getMarkID() + "_" + i + ".jpg";
        String photoiD = mark.getMarkID() + "_" + i;
        saveImageAsJPEG(bitmap, fileName);
        Photo photo = new Photo(photoiD, fileName, formattedDate, mark.getMarkID(), entrepriseID);
        mDatabase.photoDao().insert(photo);
        dismiss();  // Ferme le BottomSheetDialogFragment après la sauvegarde
    }


    private void saveImageAsJPEG(Bitmap bitmap, String fileName) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

// Charger le bitmap d'origine


// Redimensionner l'image pour correspondre à l'écran
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);

        File appDir = getContext().getFilesDir(); // Répertoire principal de l'application
        File myDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + mark.getMarkID()); // Sous-répertoire
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        File file = new File(myDir, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur lors de la sauvegarde de l'image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (valeurs != null) {
            valeurs = getValeurs(); // Récupérer les valeurs depuis le bottomSheet
//            if (nouveauMark) {
//                z.createMark(e, valeurs);
//                nouveauMark = false;
//            } else
            {
                mark.setDesignation(designation.getText().toString());
                mark.setObservation(observation.getText().toString());
                mark.setStatut(etat.getSelectedItem().toString());
                mark.setType(type.getSelectedItem().toString());
                mark.setLot(lot.getSelectedItem().toString());
                //mark.setDate(dateAjout.getText().toString());
                mark.setPriorite(priorite.getSelectedItem().toString());
                if (etat.getSelectedItem().toString().equals("SNT"))
                    view.setBackgroundColor(darkenColor(Color.RED, 0.5f));
                else if (etat.getSelectedItem().toString().equals("TNV"))
                    view.setBackgroundColor(darkenColor(Color.BLUE, 0.5f));
                else view.setBackgroundColor(darkenColor(Color.GREEN, 0.5f));
                mDatabase.markDao().update(mark);
            }
        }
    }

    private int getScreenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    public String[] getValeurs() {
        if (valeurs != null) {
            valeurs[0] = designation.getText().toString();
            valeurs[1] = observation.getText().toString();
            valeurs[2] = etat.getSelectedItem().toString();
            valeurs[3] = type.getSelectedItem().toString();
            LocalDate currentDate = LocalDate.now();

            // Formater la date au format YYYY-MM-DD
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(formatter);

            valeurs[4] = formattedDate;
            valeurs[5] = priorite.getSelectedItem().toString();
            valeurs[6] = lot.getSelectedItem().toString();
            if (valeurs[6].equals("S/O")) valeurs[6] = typeLot;
        }
        return valeurs;
    }


    private void supprimerMark() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Supprission Marque");
        builder.setMessage("la marque va être supprimée ainsi que ses photos ?");

        // Bouton Oui
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(requireContext(), "Action confirmée", Toast.LENGTH_SHORT).show();

                Runnable task = () -> {

                    z.runOnUiThread(() -> {
                        Toast.makeText(requireContext(), entrepriseID + " " + typeEntreprise, Toast.LENGTH_SHORT).show();
                        z.layout.removeView(view);
                        mDatabase.markDao().delete(mark);
                        deleteLocalFileByMark(mark);
                        valeurs = null;
                        dismiss(); // Ferme le bottom sheet
                    });
                };
                Executor executorService = Executors.newFixedThreadPool(2);
                executorService.execute(task);
            }
        });

        // Bouton Non
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(requireContext(), "Action annulée", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Fermer le dialogue
            }
        });

        // Créer et afficher le dialogue
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

