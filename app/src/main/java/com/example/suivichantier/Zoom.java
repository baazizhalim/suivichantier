package com.example.suivichantier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Zoom extends AppCompatActivity {
    private   final int PERMISSION_REQUEST_CODE = 100;
    private   final String TAG = "Zoom";
    protected   ImageView imageView1;
    private float scaleFactor = 1.0f;
    private   Matrix matrix = new Matrix();
    private   float[] matrixValues = new float[9];
    private   AppDatabase mDatabase;
    private float lastTouchX;
    private float lastTouchY;
    private Bitmap bitmap;

    protected    FrameLayout layout;
    protected   List<Mark> marks =new ArrayList<>();
    //protected   List<Mark> marksLot=new ArrayList<>();
    //protected   List<Mark> marksAffiches = new ArrayList<>();
    protected   List<MarkView> markViews = new ArrayList<>();
    private Plan selectedPlan;
    private int pagex,pagey;
    private List<Plan> plans = new ArrayList<>();
    private OkHttpClient client;
    private ProgressDialog progressDialog;
    private ExecutorService executorService;
    private Handler handler;
    private GestureDetector gestureDetector;
    private int entrepriseID ;
    private int lotID ;
    private String typeLot ;
   private Spinner spinnerPlans;
    //private Spinner spinnerFilter;
    //private Spinner spinnerFilterMark;
    //private Spinner spinnerTypeLot;
    private String nomEntreprise,typeEntreprise, nomChantier;
    private int  chantierID;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        client = new OkHttpClient();
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        Intent intent = getIntent();
        entrepriseID = intent.getIntExtra("entrepriseID",0);
        nomEntreprise = intent.getStringExtra("nomEntreprise");
        typeEntreprise = intent.getStringExtra("typeEntreprise");
        nomChantier = intent.getStringExtra("nomChantier");
        chantierID = intent.getIntExtra("chantierID",0);
        lotID = intent.getIntExtra("lotID", 0);
        typeLot = intent.getStringExtra("typeLot");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        layout = findViewById(R.id.layout);
        imageView1 = findViewById(R.id.image1);
        plans = mDatabase.planDao().getAllPlans(lotID);
        //plans = synchroniserPlans(plans);
        spinnerPlans = findViewById(R.id.spinnerPlans);

        ImageButton zoomInButton = findViewById(R.id.zoom_in_button);
        ImageButton zoomOutButton = findViewById(R.id.zoom_out_button);

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleFactor *= 1.25f;
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
                matrix.setScale(scaleFactor, scaleFactor);
                //imageView1.setImageMatrix(matrix);
                float  dx= (float) imageView1.getWidth() /2- (float)pagex/2*scaleFactor;
                float  dy= (float) imageView1.getHeight() /2- (float)pagey /2*scaleFactor;
                matrix.postTranslate(dx, dy);
                imageView1.setImageMatrix(matrix);
                markViews.forEach(Zoom.this::updateImageViewMarkPosition);

            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleFactor *= 0.8f;
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
                matrix.setScale(scaleFactor, scaleFactor);
                //imageView1.setImageMatrix(matrix);
                float  dx= (float) imageView1.getWidth() /2- (float)pagex/2*scaleFactor;
                float  dy= (float) imageView1.getHeight() /2- (float) pagey /2*scaleFactor;
                matrix.postTranslate(dx, dy);
                imageView1.setImageMatrix(matrix);
                markViews.forEach(Zoom.this::updateImageViewMarkPosition);

            }
        });




        Plan p0=new Plan(-1,"Choisir un Plan","Choisir un Plan",0);
        plans.add(0,p0);
        ArrayAdapter<Plan> adapterPlans = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plans);
        adapterPlans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlans.setAdapter(adapterPlans);


        //scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new GestureListener());

        imageView1.setOnTouchListener((v, event) -> {

            //scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    //isLongClick = false;
                    //handler.postDelayed(longClickRunnable, LONG_CLICK_DURATION);
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

                    //posX += dx;
                    //posY += dy;

                    matrix.postTranslate(dx, dy);
                    imageView1.setImageMatrix(matrix);

                    lastTouchX = x;
                    lastTouchY = y;

                    markViews.forEach(Zoom.this::updateImageViewMarkPosition);
                    break;
                }


                default:
                    return false;
            }
            return true;
        });
        spinnerPlans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedPlan = (Plan) parent.getItemAtPosition(position);
                    if (selectedPlan.getPlanID() != -1) {
                        showProgressDialog();
                        executorService.execute(() -> downloadFile());
                        markViews.forEach(markview -> {
                            layout.removeView(markview.getImageButton());
                        });
                        markViews.clear();
                        marks.clear();
                        scaleFactor = 1.0f;
                        matrix = new Matrix();
                        imageView1.setImageMatrix(matrix);
                        displayPdf(selectedPlan.getFile(), selectedPlan.getDescription(), selectedPlan.getPlanID());
                        marks = getAllMarks(selectedPlan);
                        //marks=synchroniserMarks(marks);
                        drawAllMarks(marks);
                        handler.post(Zoom.this::hideProgressDialog);
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private List<Plan> synchroniserPlans(List<Plan> plans) {
        return plans;
    }

    private void synchroniserMarks(Plan plan) {
        List<Mark> marksLocales=new ArrayList<>();
        List<Mark> marksDistantes=new ArrayList<>();

        marksLocales=getAllMarks(plan);

        List<Mark> finalMarksLocales = marksLocales;
        getAllMarksDistant(plan, marksDist -> {
            // Handle the loaded marks here
            marksDistantes.addAll(marksDist);
            verifierMarksCommunes(finalMarksLocales,marksDistantes);
            verifierMarksDistantesDifferentes(finalMarksLocales,marksDistantes);


        });


    }

    private void verifierMarksDistantesDifferentes(List<Mark> marksLocales, List<Mark> marksDistantes) {


            if (typeEntreprise.equals("ER")) {
                for (int i = 0; i < marksDistantes.size(); i++) {
                    Mark markDist = marksDistantes.get(i);

                    // Si la mlarque du serveur n'est pas présent localement, on la télécharge
                    if (!marksLocales.contains(markDist)) {
                        downloadMark(markDist);
                    }
                }

                // Vérifier si des fichiers locaux ont été supprimés du serveur on les supprime localement
                for (Mark localMark : marksLocales) {
                    if (!marksDistantes.contains(localMark)) {
                        // Le fichier a été supprimé du serveur, on le supprime localement
                        deleteLocalMark(localMark);
                    }
                }
            } else if (typeEntreprise.equals("ES")) {
                for (int i = 0; i < marksDistantes.size(); i++) {
                    Mark markDist = marksDistantes.get(i);

                    // Si la mark du serveur n'est pas présent localement, on le supprime dans le serveur
                    if (!marksLocales.contains(markDist)) {
                        deleteRemoteMark(markDist);
                    }
                }

                // Vérifier si la mark local n'existe pas dans le serveur
                for (Mark localMark : marksLocales) {
                    if (!marksDistantes.contains(localMark)) {
                        // La mark local n'existe pas dans serveur, on la televerse
                        uploadMark(localMark);
                    }
                }
            }

    }

    private void uploadMark(Mark localMark) {
        OkHttpClient okHttpClient = null;
        Gson gson = new Gson();
        String jsonString = gson.toJson(localMark);

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));


        Request request = new Request.Builder().url("http://" + MainActivity.ip + ":3000/mobile/upload/mark")
                .put(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NotNull Call call,
                    @NotNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Problème avec le serveur " + e.getMessage(), Toast.LENGTH_SHORT).show());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("resultatbody", jsonResponse);


                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Synchronisation successful ", Toast.LENGTH_SHORT).show();

                    });


                } else
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Erreur de réponse: " + response.code(), Toast.LENGTH_SHORT).show();

                    });


            }

        });

    }


    private void deleteRemoteMark(Mark markDist) {
        OkHttpClient okHttpClient = null;

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("markID", markDist.getMarkID())
                .build();

        Request request = new Request.Builder().url("http://" + MainActivity.ip + ":3000/mobile/delete/mark")
                .delete(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NotNull Call call,
                    @NotNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Problème avec le serveur " + e.getMessage(), Toast.LENGTH_SHORT).show());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("resultatbody", jsonResponse);


                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Synchronisation successful ", Toast.LENGTH_SHORT).show();

                    });


                } else
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Erreur de réponse: " + response.code(), Toast.LENGTH_SHORT).show();

                    });


            }

        });


    }

    private void deleteLocalMark(Mark localMark) {
        List<String> fichiersPhotos ;
        fichiersPhotos = getFichierPhotos(localMark);
        mDatabase.markDao().delete(localMark);
        File appDir = getApplicationContext().getExternalFilesDir(null);
        for(String fichier:fichiersPhotos){
            File file = new File(appDir, "photos/" + localMark.getMarkID() + "/" + fichier);
            if (file.exists()) {
                file.delete();
            }
        }


    }

    private void downloadMark(Mark markDist) {

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/getMark/unique/" + markDist.getMarkID())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Download failed: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(Zoom.this, "Download failed", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server returned error: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(Zoom.this, "Server error", Toast.LENGTH_SHORT).show();

                    });
                    return;
                }

                final GsonBuilder builder = new GsonBuilder();
                final Gson gson = builder.create();

                String jsonResponse = response.body().string();

                Mark mark = gson.fromJson(jsonResponse, Mark.class);
                mDatabase.markDao().insert(mark);

                runOnUiThread(() -> {
                    Toast.makeText(Zoom.this, "mark récuperée", Toast.LENGTH_SHORT).show();

                });
            }
        });

    }

    private List<String> getFichierPhotos(Mark localMark) {
        List<String> fichiers;
        fichiers = mDatabase.photoDao().getAllfilesMark(localMark.getMarkID());
        return fichiers;

    }


    private void verifierMarksCommunes(List<Mark> marksLocales, List<Mark> marksDistantes) {
        for (Mark localMark : marksLocales) {
            if (marksDistantes.contains(localMark)) {
                Mark markDist=getMark(marksDistantes,localMark.getMarkID());
                if(localMark.getStatut().equals("SNT") && !markDist.getStatut().equals("SNT")){
                    updateLocalMark(localMark,markDist.getStatut());
                }
                else if(!localMark.getStatut().equals("SNT") && markDist.getStatut().equals("SNT")){
                    updateDistMark(markDist,localMark.getStatut());
                }
                else if(localMark.getStatut().equals("TNV") && markDist.getStatut().equals("TV")){
                    updateLocalMark(localMark,"TV");
                }
                else if(localMark.getStatut().equals("TV") && markDist.getStatut().equals("TNV")){
                    updateDistMark(markDist,"TV");
                }

            }
        }


    }

    private void updateDistMark(Mark markDist, String statut) {

        OkHttpClient client = new OkHttpClient();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("statut", statut)
                .addFormDataPart("markID", markDist.getMarkID())
                .build();

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/marksynchro/")
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "synchro", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "no synchro", Toast.LENGTH_SHORT).show());
                }
            }
        });

    }

    private void updateLocalMark(Mark localMark, String statut) {
        localMark.setStatut(statut);
        mDatabase.markDao().update(localMark);

    }

    private Mark getMark(List<Mark> marksListe, String markID) {
        Mark mark=null;
        for(Mark markrech:marksListe){
            if(markrech.getMarkID().equals(markID)){
                mark=markrech;
                break;
            }
        }

        return mark;
    }

    public interface OnMarksLoadedListener {
        void onMarksLoaded(List<Mark> marks);
    }

    private void getAllMarksDistant(Plan plan, OnMarksLoadedListener listener) {
        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/getMarks/" + plan.getPlanID())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Download failed: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(Zoom.this, "Download failed", Toast.LENGTH_SHORT).show();
                    listener.onMarksLoaded(Collections.emptyList()); // Pass empty list on failure
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server returned error: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(Zoom.this, "Server error", Toast.LENGTH_SHORT).show();
                        listener.onMarksLoaded(Collections.emptyList()); // Pass empty list on error
                    });
                    return;
                }

                final GsonBuilder builder = new GsonBuilder();
                final Gson gson = builder.create();

                String jsonResponse = response.body().string();
                Type markListType = new TypeToken<List<Mark>>() {}.getType();
                List<Mark> marks = gson.fromJson(jsonResponse, markListType);

                runOnUiThread(() -> {
                    listener.onMarksLoaded(marks); // Pass the marks back via the listener
                });
            }
        });
    }


    private  void  downloadFile(){
        File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
        File subDir = new File(appDir, selectedPlan.getDescription()); // Sous-répertoire

        File file = new File(subDir, selectedPlan.getFile());
        if(!file.exists()){
            runOnUiThread(() -> {
                        Toast.makeText(Zoom.this, "telechargement du fichier ", Toast.LENGTH_SHORT).show();
                    });
            downloadFile("http://" + MainActivity.ip + ":3000/download", selectedPlan.getFile(), selectedPlan.getDescription());
    }
        else {
            runOnUiThread(() -> {
                Toast.makeText(Zoom.this, "fichier local ", Toast.LENGTH_SHORT).show();
            });
        }

    }

    private  void downloadFile(String url, String fileName, String type) {
        Request request = new Request.Builder().url(url + "/" + type + "/" + fileName).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Download failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(Zoom.this, "Download failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server returned error: " + response.code());
                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Server error", Toast.LENGTH_SHORT).show());
                    return;
                }

                File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
                File subDir = new File(appDir, type); // Sous-répertoire

                // Créez le sous-répertoire s'il n'existe pas
                if (!subDir.exists()) {
                    if (!subDir.mkdirs()) {
                        Log.e(TAG, "Failed to create directory");
                        runOnUiThread(() -> Toast.makeText(Zoom.this, "Failed to create directory", Toast.LENGTH_SHORT).show());
                        return;
                    }
                }

                File file = new File(subDir, fileName);
                try (InputStream inputStream = response.body().byteStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Download complete", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    Log.e(TAG, "Error saving file: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Error saving file", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Veuillez patienter...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected  void createMark(MotionEvent e, String[] valeurs) {
        if (valeurs != null) {
        matrix.getValues(matrixValues);
        float posX = (e.getX() -matrixValues[Matrix.MTRANS_X])/ matrixValues[Matrix.MSCALE_X] ;
        float posY = (e.getY() -matrixValues[Matrix.MTRANS_Y])/ matrixValues[Matrix.MSCALE_Y];
        Mark mark = new Mark("mark_" + e.getX() + "_" + e.getY()+"_"+imageView1.getId(), valeurs[0], valeurs[3], valeurs[6], (int) posX, (int) posY, valeurs[1], valeurs[2], valeurs[5], valeurs[4], imageView1.getId());
        addMarker(mark);
        // traitement de creation d'une mark
        }


    }

    public void afficherMarkFiltres(List<Mark> listeMarks) {
        showProgressDialog();
        //executorService.execute(() -> downloadFile());
        markViews.forEach(markview -> {
            layout.removeView(markview.getImageButton());
        });
        markViews.clear();
        marks.clear();
        marks.addAll(listeMarks);
        scaleFactor = 1.0f;
        matrix = new Matrix();
        imageView1.setImageMatrix(matrix);
        //displayPdf(selectedPlan.getFile(), selectedPlan.getDescription(), selectedPlan.getPlanID());
        //marks = getAllMarks(selectedPlan.getPlanID());
        //marks=synchroniserMarks(marks);
        drawAllMarks(marks);
        handler.post(Zoom.this::hideProgressDialog);

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            //imageView1.setImageMatrix(matrix);
            float  dx= (float) imageView1.getWidth() /2- (float)bitmap.getWidth()/2;
            float  dy= (float) imageView1.getHeight() /2- (float) bitmap.getHeight() /2;
            matrix.postTranslate(dx, dy);
            imageView1.setImageMatrix(matrix);
            markViews.forEach(Zoom.this::updateImageViewMarkPosition);
            return false;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Gérer le double clic ici
            handleDoubleClick(e);
            return false; //super.onDoubleTap(e);
        }
    }

    private void handleDoubleClick(MotionEvent e) {

            afficherFenetreMark(null,e);

    }

    private   boolean drawMark(Mark mark) {
        ImageButton icone = new ImageButton(imageView1.getContext());

// Assurez-vous que 'mark' n'est pas nul et vérifiez le type de marque
        if (mark != null) {
            switch (mark.getType()) {
                case "reserve":
                    icone.setImageResource(R.drawable.icons8_exclamation_48);
                    break;
                case "tache":
                    icone.setImageResource(R.drawable.icons8_checklist_48);
                    break;
                case "note":
                    icone.setImageResource(R.drawable.icons8_note_40);
                    break;
                default:
                    // Ajoutez un cas par défaut pour gérer les types inconnus
                    Log.e("Zoom", "Type de marque inconnu: " + mark.getType());
                    break;
            }
            icone.setContentDescription(mark.getMarkID());
            if(mark.getStatut().equals("SNT"))icone.setBackgroundColor(Color.RED);
            else if (mark.getStatut().equals("TNV"))icone.setBackgroundColor(Color.BLUE);
            else icone.setBackgroundColor(Color.GREEN);
            // Ajoutez l'imageView au layout


            // Assurez-vous que layout n'est pas nul et est bien un FrameLayout
            if (layout != null) {
                layout.addView(icone);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) icone.getLayoutParams();
                params.width = 60;
                params.height = 60;
                params.leftMargin = (int) mark.getPosx();
                params.topMargin = (int) mark.getPosy();
                icone.setLayoutParams(params);

                //traitement de l'icone mark
                MarkView markView=new MarkView(mark, icone);
                markViews.add(markView);
                icone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //MarkView mv= markViews.stream().filter(markView -> markView.getImageButton().equals(view)).findFirst().orElse(null);
                        afficherFenetreMark(markView,null);
                    }
                });

                updateImageViewMarkPosition(markView);
            } else {
                Log.e("Zoom", "Le layout n'est pas un FrameLayout");
                return false;
            }
        } else {
            Log.e("Zoom", "Mark est null");
            return false;
        }
        return true;

    }

    public   void afficherFenetreMark(MarkView mv, MotionEvent e) {
        MyBottomSheetDialogFragment bottomSheet = new MyBottomSheetDialogFragment(mv, e,this, entrepriseID, typeEntreprise,typeLot);
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheet");

         }

    private void updateImageViewMarkPosition(MarkView markView) {
        matrix.getValues(matrixValues);
        float scaledX = markView.getMark().getPosx() * matrixValues[Matrix.MSCALE_X] + matrixValues[Matrix.MTRANS_X];
        float scaledY = markView.getMark().getPosy() * matrixValues[Matrix.MSCALE_Y] + matrixValues[Matrix.MTRANS_Y];
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) markView.getImageButton().getLayoutParams();
        params.leftMargin = (int) scaledX;
        params.topMargin = (int) scaledY;
        markView.getImageButton().setLayoutParams(params);
    }

    private   void addMarker(Mark mark) {
        marks.add(mark);
        boolean ok=drawMark(mark);
        if (ok) mDatabase.markDao().insert(mark);

    }

    protected List<Mark> getAllMarks(Plan plan) {
        List<Mark> marks;
        marks = mDatabase.markDao().getAllMarks(plan.getPlanID());
        return marks;
    }

    private void drawAllMarks(List<Mark> marks) {
        marks.forEach(this::drawMark);
    }

    protected   MarkView findMarker(String markID){
        return markViews.stream().filter(markView -> markView.getMark().getMarkID().equals(markID)).findFirst().orElse(null);
    }

    private void displayPdf(String fichier, String type, int planID) {
        try {
            File appDir = getExternalFilesDir(null); // Répertoire principal de l'application
            File subDir = new File(appDir, type); // Sous-répertoire

            File file = new File(subDir, fichier);
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(0); // Ouvrir la première page
            pagex=page.getWidth();
            pagey=page.getHeight();
            // Créer un bitmap pour rendre la page PDF
            bitmap = Bitmap.createBitmap(pagex,pagey, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Afficher la page rendue dans l'ImageView
            imageView1.setImageBitmap(bitmap);
            imageView1.setId(planID);
            float  dx= (float) imageView1.getWidth() /2- (float) bitmap.getWidth() /2;
            float  dy= (float) imageView1.getHeight() /2- (float) bitmap.getHeight() /2;
            matrix.postTranslate(dx, dy);
            scaleFactor= (float) imageView1.getWidth() / (float) bitmap.getWidth() ;
            matrix.setScale(scaleFactor, scaleFactor);
            imageView1.setImageMatrix(matrix);

            // Fermer la page et le renderer
            page.close();
            pdfRenderer.close();
            fileDescriptor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //startDownload();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_search) {
//
//            return true;
//        } else

        if (id == R.id.filtre) {
            afficherFenetrefiltre(selectedPlan);
            return true;
        }else if (id == R.id.action_synch) {
            synchroniserMarks(selectedPlan);
            afficherMarkFiltres(marks);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void synchroLocale() {
    }

    private void afficherMark() {
    }

    private void filtrer() {
    }

    public   void afficherFenetrefiltre(Plan  plan) {
        MyBottomSheetDialogFragmentFilter bottomSheet = new MyBottomSheetDialogFragmentFilter(plan,this, entrepriseID, typeEntreprise,typeLot);
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheetFilter");

    }

}

