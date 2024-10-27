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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    protected MarkView markView;
    protected ImageView view = null;
    protected Mark mark = null;
    protected int entrepriseID;
    protected String typeEntreprise;
    protected String typeLot;
    protected AppDatabase mDatabase;
    protected MotionEvent e;
    TextView title;
    EditText designation;
    EditText observation;
    Spinner etat;
    Spinner type;
    Spinner lot;
    EditText dateAjout;
    Spinner priorite;
    Zoom z;
    Button buttonCancel;
    Button buttonDelete;
    Button buttonOK;
    Button buttonModifier;
    Button buttonSynchroMark;
    int statut = 0;
    boolean nouveauMark = false;
    private String[] valeurs = new String[7];
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Bitmap capturedImage;
    protected LinearLayout barePhoto;
    protected TableRow[] rows = new TableRow[2];
    protected int i = 0;
    //private float scaleFactor = 1.0f;
    //private Matrix matrix = new Matrix();


    public MyBottomSheetDialogFragment(MarkView markView, MotionEvent e, Zoom z, int entrepriseID, String typeEntreprise, String typelot) {
        this.markView = markView;
        this.e = e;
        this.z = z;
        this.entrepriseID = entrepriseID;
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
        View fenetre = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        mDatabase = Room.databaseBuilder(getContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        rows[0] = fenetre.findViewById(R.id.row1);
        rows[1] = fenetre.findViewById(R.id.row2);

        Button btnCapture = fenetre.findViewById(R.id.btnCapture);
        btnCapture.setEnabled(false);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    openCamera();
                }
            }
        });


        // Initialisez vos vues ici
        title = fenetre.findViewById(R.id.bottom_sheet_title);
        designation = fenetre.findViewById(R.id.designation);
        observation = fenetre.findViewById(R.id.observation);
        etat = fenetre.findViewById(R.id.etat);
        type = fenetre.findViewById(R.id.type);
        lot = fenetre.findViewById(R.id.lot);
        dateAjout = fenetre.findViewById(R.id.dateAjout);
        priorite = fenetre.findViewById(R.id.priorite);

        List<String> options1 = new ArrayList<>();
        options1.add("SNT");
        options1.add("TNV");
        options1.add("TV");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, options1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etat.setAdapter(adapter1);


        List<String> options2 = new ArrayList<>();
        options2.add("reserve");
        options2.add("tache");
        options2.add("note");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, options2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter2);

        List<String> lots = new ArrayList<>();
        lots.add("S/O");
        lots.add("Maçonnerie");
        lots.add("Ménuiserie");
        lots.add("Plomberie");
        lots.add("enduit");
        lots.add("revetement");
        lots.add("electricite");
        lots.add("Equipements");
        lots.add("Peinture");
        lots.add("Etancheite");
        lots.add("Autres");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, lots);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lot.setAdapter(adapter4);
        lot.setEnabled(false);
        if (typeLot.equals("CES")) lot.setEnabled(true);

        List<String> options3 = new ArrayList<>();
        options3.add("Normale");
        options3.add("Urgente");

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, options3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorite.setAdapter(adapter3);

        buttonOK = fenetre.findViewById(R.id.bottom_ok_button);
        buttonModifier = fenetre.findViewById(R.id.bottom_modifier_button);
        buttonDelete = fenetre.findViewById(R.id.bottom_delete_button);
        if (typeEntreprise.equals("ER")) buttonDelete.setEnabled(false);
        buttonCancel = fenetre.findViewById(R.id.bottom_cancel_button);
        buttonSynchroMark = fenetre.findViewById(R.id.btnSynchroMark);

        if (mark != null) {
            if (!typeEntreprise.equals("ER")) btnCapture.setEnabled(true);
            designation.setText(mark.getDesignation());
            observation.setText(mark.getObservation());
            etat.setSelection(options1.indexOf(mark.getStatut()));
            type.setSelection(options2.indexOf(mark.getType()));
            lot.setSelection(lots.indexOf(mark.getLot()));
            dateAjout.setText(mark.getDate());
            priorite.setSelection(options3.indexOf(mark.getPriorite()));
            if (etat.getSelectedItem().toString().equals("SNT"))
                buttonModifier.setBackgroundColor(Color.RED);
            else if (etat.getSelectedItem().toString().equals("TNV"))
                buttonModifier.setBackgroundColor(Color.BLUE);
            else buttonModifier.setBackgroundColor(Color.GREEN);
            afficherBoutonPhotos(mark.getMarkID());
        } else {
            btnCapture.setEnabled(false);
            buttonSynchroMark.setEnabled(false);
            buttonModifier.setBackgroundColor(Color.RED);

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

        buttonModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etat.getSelectedItem().toString().equals("SNT") && mark != null) {
                    buttonModifier.setBackgroundColor(Color.BLUE);
                    etat.setSelection(1);
                } else if (etat.getSelectedItem().toString().equals("TNV")) {
                    buttonModifier.setBackgroundColor(Color.GREEN);
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
                valeurs = null;
                dismiss(); // Ferme le bottom sheet
            }
        });
        buttonSynchroMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                synchroniserPhotos(mark);

                //dismiss(); // Ferme le bottom sheet
            }
        });


        return fenetre;
    }

    private void afficherBoutonPhotos(String markID) {
        rows[0].removeAllViews();
        rows[1].removeAllViews();
        List<Photo> photos = mDatabase.photoDao().getAllPhotosMark(markID);
        Button[] photoButtons = new Button[2];
        Button[] photoDeletes = new Button[2];
        //Button[] photoSynchs = new Button[2];
        i = 0;
        for (Photo photo : photos) {

            photoButtons[i] = new Button(getContext());
            photoButtons[i].setText(photo.getFile());
            photoButtons[i].setContentDescription(String.valueOf(photo.getPhotoID()));
            rows[i].addView(photoButtons[i]);
            photoDeletes[i] = new Button(getContext());
            photoDeletes[i].setText("Delete");
            photoDeletes[i].setContentDescription(String.valueOf(photo.getPhotoID()));
            if (typeEntreprise.equals("ER")) photoDeletes[i].setEnabled(false);
            rows[i].addView(photoDeletes[i]);
            //photoSynchs[i] = new Button(getContext());
            //photoSynchs[i].setText("Synchro");
            //photoSynchs[i].setContentDescription(String.valueOf(photo.getPhotoID()));
            //rows[i].addView(photoSynchs[i]);

            photoButtons[i].setOnClickListener(new View.OnClickListener() {
                float scaleFactor = 1.0f;
                Matrix matrix = new Matrix();

                @Override
                public void onClick(View v) {

                    File appDir = getContext().getExternalFilesDir(null); // Répertoire principal de l'application
                    File myDir = new File(appDir, "/photos"); // Sous-répertoire
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
                    ImageButton zoomInButton = dialog.findViewById(R.id.zoom_in_button);
                    ImageButton zoomOutButton = dialog.findViewById(R.id.zoom_out_button);
                    ImageButton pen = dialog.findViewById(R.id.pen);
                    ImageButton close = dialog.findViewById(R.id.close);
                    // Affichez l'image chargée dans l'ImageView
                    imageView.setImageBitmap(imageBitmap);
//                        scaleFactor = (float) imageView.getWidth() / (float) imageBitmap.getWidth();
//                        matrix.setScale(scaleFactor, scaleFactor);
//                        float dx = (float) imageView.getWidth() / 2 - (float) imageBitmap.getWidth() / 2 * scaleFactor;
//                        float dy = (float) imageView.getHeight() / 2 - (float) imageBitmap.getHeight() / 2 * scaleFactor;
//                        matrix.postTranslate(dx, dy);
//                        imageView.setImageMatrix(matrix);
                    zoomInButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scaleFactor *= 1.25f;
                            scaleFactor = Math.max(0.3f, Math.min(scaleFactor, 8.0f));
                            matrix.setScale(scaleFactor, scaleFactor);
                            float dx = (float) imageView.getWidth() / 2 - (float) imageBitmap.getWidth() / 2 * scaleFactor;
                            float dy = (float) imageView.getHeight() / 2 - (float) imageBitmap.getHeight() / 2 * scaleFactor;
                            matrix.postTranslate(dx, dy);
                            imageView.setImageMatrix(matrix);


                        }
                    });

                    zoomOutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scaleFactor *= 0.8f;
                            scaleFactor = Math.max(0.3f, Math.min(scaleFactor, 8.0f));
                            matrix.setScale(scaleFactor, scaleFactor);
                            float dx = (float) imageView.getWidth() / 2 - (float) imageBitmap.getWidth() / 2 * scaleFactor;
                            float dy = (float) imageView.getHeight() / 2 - (float) imageBitmap.getHeight() / 2 * scaleFactor;
                            matrix.postTranslate(dx, dy);
                            imageView.setImageMatrix(matrix);

                        }
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
            photoDeletes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteLocalFile(photo.getFile());


                }
            });

            i++;
            if (i == 2) break;
        }


    }


    private void synchroniserPhotos(Mark mark) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/download/" + mark.getMarkID())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);


                        // Obtenir la liste des fichiers du serveur
                        JSONArray serverFiles = jsonObject.getJSONArray("files");

                        // Liste des fichiers présents localement sur le client
                        List<String> localFiles = getLocalFiles(mark.getMarkID());

                        // Parcourir les fichiers du serveur
                        if (typeEntreprise.equals("ER")) {
                            for (int i = 0; i < serverFiles.length(); i++) {
                                String serverFile = serverFiles.getString(i);

                                // Si le fichier du serveur n'est pas présent localement, on le télécharge
                                if (!localFiles.contains(serverFile)) {
                                    downloadFile(serverFile);
                                }
                            }

                            // Vérifier si des fichiers locaux ont été supprimés du serveur on les supprime localement
                            for (String localFile : localFiles) {
                                if (!serverFiles.toString().contains(localFile)) {
                                    // Le fichier a été supprimé du serveur, on le supprime localement
                                    deleteLocalFile(localFile);
                                }
                            }
                        } else if (typeEntreprise.equals("ES")) {
                            for (int i = 0; i < serverFiles.length(); i++) {
                                String serverFile = serverFiles.getString(i);

                                // Si le fichier du serveur n'est pas présent localement, on le supprime
                                if (!localFiles.contains(serverFile)) {
                                    deleteRemoteFile(serverFile);
                                }
                            }

                            // Vérifier si des fichiers locaux ont été supprimés du serveur
                            for (String localFile : localFiles) {
                                if (!serverFiles.toString().contains(localFile)) {
                                    // Le fichier local a été supprimé du serveur, on le televerse
                                    uploadFile(localFile);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void deleteRemoteFile(String remoteFile) {
        OkHttpClient client = new OkHttpClient();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", remoteFile)
                .addFormDataPart("markID", mark.getMarkID())
                .build();

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/upload/delete/photo/" + entrepriseID)
                .delete(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                z.runOnUiThread(() -> Toast.makeText(requireActivity(), "Échec de l'upload", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    z.runOnUiThread(() -> Toast.makeText(requireActivity(), "Fichier supprimé avec succès", Toast.LENGTH_SHORT).show());
                } else {
                    z.runOnUiThread(() -> Toast.makeText(requireActivity(), "Erreur lors de l'upload", Toast.LENGTH_SHORT).show());
                }
            }
        });

    }

    private void uploadFile(String fileName) {
        OkHttpClient client = new OkHttpClient();
        File appDir = getContext().getExternalFilesDir(null);
        File file = new File(appDir, "photos/" + mark.getMarkID() + "/" + fileName);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("markID", mark.getMarkID())
                .build();

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/upload/uploadphoto/photo/" + entrepriseID)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                z.runOnUiThread(() -> Toast.makeText(requireActivity(), "Échec de l'upload", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    z.runOnUiThread(() -> Toast.makeText(requireActivity(), "Fichier uploadé avec succès", Toast.LENGTH_SHORT).show());
                } else {
                    z.runOnUiThread(() -> Toast.makeText(requireActivity(), "Erreur lors de l'upload", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    private void downloadFile(String fileName) {
        // Implémentation pour télécharger le fichier depuis le serveur (via HTTP )
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/download/photos/" + fileName)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Sauvegarder le fichier téléchargé localement

                    saveFileLocally(response.body().byteStream(), fileName);

                }
            }
        });
    }

    // Fonction pour supprimer un fichier localement
    private void deleteLocalFile(String fileName) {
        File appDir = getContext().getExternalFilesDir(null);
        File file = new File(appDir, "photos/" + mark.getMarkID() + "/" + fileName);
        if (file.exists()) {
            file.delete();
            deleteLocalyFilefromDB(fileName);
        }
    }

    private void deleteLocalyFilefromDB(String fileName) {
        mDatabase.photoDao().deletePhoto(fileName);

    }


    // Fonction pour sauvegarder un fichier localement
    private void saveFileLocally(InputStream inputStream, String fileName) {
        try {
            File appDir = getContext().getExternalFilesDir(null);
            File file = new File(appDir, "photos/" + mark.getMarkID() + "/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            saveFilelocalyToDB(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFilelocalyToDB(String fileName) {
        int currentdate = (int) new Date().getTime();
        int i = getNumeroPhotoDisponible(mark.getMarkID());
        String photoiD = mark.getMarkID() + "_" + i;
        Photo photo = new Photo(photoiD, fileName, String.valueOf(currentdate), mark.getMarkID(), entrepriseID);
        mDatabase.photoDao().insert(photo);

    }

    private List<String> getLocalFiles(String markID) {
        File appDir = getContext().getExternalFilesDir(null);

        File directory = new File(appDir, "/photos/" + markID + "/");
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
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
        }

        return num;
    }

    private void saveImage(Bitmap bitmap) {
        int currentdate = (int) new Date().getTime();
        int i = getNumeroPhotoDisponible(mark.getMarkID());
        String fileName = "photo_" + mark.getMarkID() + "_" + i + ".jpg";
        String photoiD = mark.getMarkID() + "_" + i;
        saveImageAsJPEG(bitmap, fileName);
        Photo photo = new Photo(photoiD, fileName, String.valueOf(currentdate), mark.getMarkID(), entrepriseID);
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

        File appDir = getContext().getExternalFilesDir(null); // Répertoire principal de l'application
        File myDir = new File(appDir, "/photos/" + mark.getMarkID()); // Sous-répertoire
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
            if (nouveauMark) {
                z.createMark(e, valeurs);
                nouveauMark = false;
            } else {
                mark.setDesignation(designation.getText().toString());
                mark.setObservation(observation.getText().toString());
                mark.setStatut(etat.getSelectedItem().toString());
                mark.setType(type.getSelectedItem().toString());
                mark.setLot(lot.getSelectedItem().toString());
                mark.setDate(dateAjout.getText().toString());
                mark.setPriorite(priorite.getSelectedItem().toString());
                if (etat.getSelectedItem().toString().equals("SNT"))
                    view.setBackgroundColor(Color.RED);
                else if (etat.getSelectedItem().toString().equals("TNV"))
                    view.setBackgroundColor(Color.BLUE);
                else view.setBackgroundColor(Color.GREEN);
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
            valeurs[4] = dateAjout.getText().toString();
            valeurs[5] = priorite.getSelectedItem().toString();
            valeurs[6] = lot.getSelectedItem().toString();
            if(valeurs[6].equals("S/O")) valeurs[6] = typeLot;
        }
        return valeurs;
    }
}

