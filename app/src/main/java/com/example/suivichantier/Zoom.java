package com.example.suivichantier;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import kotlin.collections.UArraySortingKt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Zoom extends AppCompatActivity {

    private     final int PERMISSION_REQUEST_CODE = 100;
    private     final String TAG = "Zoom";
    protected   ImageView imageView1;
    private     float scaleFactor = 1.0f;
    private     Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private     AppDatabase mDatabase;
    private     float lastTouchX;
    private     float lastTouchY;
    private     Bitmap bitmap;
    protected   FrameLayout layout;
    protected   List<Mark> marks =new ArrayList<>();
    protected   List<Mark> marksAffiches = new ArrayList<>();
    protected   List<MarkView> markViews = new ArrayList<>();
    public      Plan selectedPlan;
    private     int pagex,pagey;
    private     List<Plan> plans = new ArrayList<>();
    //private     OkHttpClient client;
    private     ProgressDialog progressDialog;
    private     ExecutorService executorService;
    private     ScaleGestureDetector scaleGestureDetector;
    private     Handler handler;
    private     GestureDetector gestureDetector;
    private     int entrepriseID ;
    private     int lotID ;
    private     String typeLot ;
    protected     Spinner spinnerPlans;
    private     ArrayAdapter<Plan> adapterPlans;
    private     String nomEntreprise,typeEntreprise, nomClient,nomChantier;
    private     int  chantierID;
    public static boolean isFirstSelection;
    public boolean bottomSheetclose;
    protected boolean isBottomSheetOpen=false;
    private float dx;
    private float dy;
    //private boolean init=true;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);

        //client = new OkHttpClient();
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        handler = new Handler(Looper.getMainLooper());
        Intent intent = getIntent();
        entrepriseID = intent.getIntExtra("entrepriseID",0);
        nomEntreprise = intent.getStringExtra("nomEntreprise");
        typeEntreprise = intent.getStringExtra("typeEntreprise");
        nomChantier = intent.getStringExtra("nomChantier");
        nomClient = intent.getStringExtra("nomClient");
        chantierID = intent.getIntExtra("chantierID",0);
        lotID = intent.getIntExtra("lotID", 0);
        typeLot = intent.getStringExtra("typeLot");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }


        layout = findViewById(R.id.layout);
        imageView1 = findViewById(R.id.image1);
        plans = mDatabase.planDao().getAllPlans(lotID);
        Plan p0=new Plan(-1,"Plan","Plan",0);
        selectedPlan=p0;
        plans.add(0,p0);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new GestureListener());

        imageView1.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);

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

                    //init=false;
                    final float x = event.getX();
                    final float y = event.getY();

                     dx = x - lastTouchX;
                     dy = y - lastTouchY;



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

        bottomSheetclose=false;


    }

    private void synchroniserPlans() {
            showProgressDialog();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("lotID", lotID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Créer une requête HTTP POST avec le corps JSON
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));


            Request request = new Request.Builder().url("http://" + MainActivity.ip + ":3000/mobile/synchro/plans/")
                    .post(body)
                    .build();
            OkHttpClient okHttpClient=new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(
                        @NotNull Call call,
                        @NotNull IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Problème avec le serveur " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    handler.post(Zoom.this::hideProgressDialog);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        Log.d("resultatbody", jsonResponse);

                        final GsonBuilder builder = new GsonBuilder();
                        final Gson gson = builder.create();

                        // Désérialiser result1 en une liste d'objets User

                        Type planListType = new TypeToken<List<Plan>>() {
                        }.getType();
                        List<Plan> plansSync = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("PLANS"), planListType);
                        Log.d("resultatbody", plansSync.toString());
                        Type markListType = new TypeToken<List<Mark>>() {
                        }.getType();
                        List<Mark> marksSync = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("marks"), markListType);
                        Log.d("resultatbody", marksSync.toString());
                        Type photoListType = new TypeToken<List<Photo>>() {
                        }.getType();
                        List<Photo> photosSync = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("photos"), photoListType);
                        Log.d("resultatbody", photosSync.toString());
                        //mDatabase mettre a jour database;
                        mDatabase.runInTransaction(() -> {
                            mDatabase.planDao().deletePlan(lotID);
                            mDatabase.planDao().insertAll(plansSync);
                            mDatabase.markDao().insertAll(marksSync);
                            mDatabase.photoDao().insertAll(photosSync);
                            File appDir = getFilesDir();
                            String path=nomEntreprise + "/" +nomChantier + "/" +typeLot + "/";
                            File file = new File(appDir, path);
                            viderReportoire(file);
                            plansSync.forEach(plan -> {
                                CompletableFuture<Boolean> downloadFuture = downloadFileAsync(
                                        Zoom.this,
                                        new OkHttpClient(),
                                        "http://" + MainActivity.ip + ":3000/download",
                                        plan.getFile()
                                );
                                downloadFuture.thenAccept(success -> {
                                    runOnUiThread(() -> {

                                        if (success) {

                                        }
                                         else {
                                            Toast.makeText(Zoom.this, "Téléchargement échoué", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }).exceptionally(ex -> {
                                    runOnUiThread(() -> {

                                        Toast.makeText(Zoom.this, "Erreur : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                    return null;
                                });

                            });
                            });


                        runOnUiThread(() -> {
                            markViews.forEach(markview -> layout.removeView(markview.getImageButton()));
                            markViews.clear();
                            marks.clear();
                            plans.clear();
                            plans.addAll(mDatabase.planDao().getAllPlans(lotID));
                            Plan p0=new Plan(-1,"Plan","Plan",0);
                            plans.add(0,p0);
                            spinnerPlans.setSelection(0);
                            imageView1.setImageBitmap(null);
                            imageView1.setId(0);
                            layout.invalidate();
                            Toast.makeText(Zoom.this, "Synchronisation successful ", Toast.LENGTH_SHORT).show();
                            handler.post(Zoom.this::hideProgressDialog);
                        });


                    } else
                        runOnUiThread(() -> {
                            Toast.makeText(Zoom.this, "Erreur de réponse: " + response.code(), Toast.LENGTH_SHORT).show();
                            handler.post(Zoom.this::hideProgressDialog);
                        });


                }

            });

    }

    private void viderReportoire(File file) {
        File [] fichiers = file.listFiles();
        if(fichiers!=null)  for (File fichier : fichiers) if(fichier.isDirectory())viderReportoire(fichier); else fichier.delete();
    }

    private void effacerPlantByTypeLot(String typeLot) {
        File appDir = getFilesDir();
        File file = new File(appDir, nomEntreprise + "/" +nomChantier + "/" +typeLot + "/");
        File [] fichiers = file.listFiles();
        if(fichiers!=null) {
            for (File fichier : fichiers) {
                if (fichier.exists()) {
                    fichier.delete();
                }
            }
        }
    }

    private void effacerFilePhotosByTypeLot(String typeLot) {
        File appDir = getFilesDir();
        File file = new File(appDir, nomEntreprise + "/" +nomChantier + "/" +typeLot + "/" +"photos/");
        File [] fichiers = file.listFiles();
        if(fichiers!=null) {
            for (File fichier : fichiers) {
                if (fichier.exists()) {
                    fichier.delete();
                }
            }
        }
    }

    public interface OnMarksLoadedListener {
        void onMarksLoaded(List<Mark> marks);
    }

    private void getAllMarksDistant(Plan plan, OnMarksLoadedListener listener) {

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/getMarks/" + plan.getPlanID())
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
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
                List<Mark> marksgson = gson.fromJson(jsonResponse, markListType);
                listener.onMarksLoaded(marksgson); // Pass the marks back via the listener

            }
        });
    }

    protected List<Mark> getAllMarksLocales(int planID) {
        List<Mark> marks;
        marks = mDatabase.markDao().getAllMarks(planID);
        return marks;
    }

    private void synchroniserMarks(Plan plan, OnMarksLoadedListener listener) {

        if(selectedPlan.getPlanID()!=-1) {

                getAllMarksDistant(plan, marksDist -> {
                    Log.d(TAG, "taille liste distabnte returned: " + marksDist.size());

                List<Mark> marksLocales = getAllMarksLocales(selectedPlan.getPlanID());
                    Log.d(TAG, "taille liste locale returned: " + marksLocales.size());
                List<Mark> marksDistantes = new ArrayList<>(marksDist);

                Thread t1 = new Thread(() -> verifierMarksCommunes(marksLocales, marksDistantes));
                t1.start();
                Thread t2 = new Thread(() -> {
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    verifierMarksDistantesDifferentes(marksLocales, marksDistantes);
                });


                t2.start();

                try {
                    t2.join();
                   // mDatabase.markDao().deleteAllMarks(plan.getPlanID());
                    listener.onMarksLoaded(mDatabase.markDao().getAllMarks(selectedPlan.getPlanID()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            });
        }

    }

    private void verifierMarksDistantesDifferentes(List<Mark> marksLocales, List<Mark> marksDistantes) {


            if (typeEntreprise.equals("ER")) {
                for (int i = 0; i < marksDistantes.size(); i++) {
                    Mark markDist = marksDistantes.get(i);

                    // Si la marque du serveur n'est pas présent localement, on la télécharge
                    if (!marksLocales.contains(markDist)) {
                        Log.d(TAG, "markdist a récupérer du serveur returned: " + markDist.getDesignation());
                        getDistMarkWithPhotos(markDist);

                    }
                }

                // Vérifier si des marques locales ont été supprimés du serveur on les supprime localement
                for (Mark localMark : marksLocales) {
                    if (!marksDistantes.contains(localMark)) {
                        Log.d(TAG, "localMark a supprimer  localement  returned: " + localMark.getDesignation());

                        deleteLocalMarkWithPhotos(localMark);
                    }
                }
            } else if (typeEntreprise.equals("ES")||typeEntreprise.equals("client")) {
                for (int i = 0; i < marksDistantes.size(); i++) {
                    Mark markDist = marksDistantes.get(i);

                    // Si la mark du serveur n'est pas présent localement, on le supprime dans le serveur
                    if (!marksLocales.contains(markDist)) {
                        Log.d(TAG, "markdist a supprimer  au serveur  returned: " + markDist.getDesignation());
                        if(markDist.getEntrepriseID()!=entrepriseID)getDistMarkWithPhotos(markDist);
                        else deleteRemoteMarkWithPhotos(markDist);
                    }
                }

                // Vérifier si la mark local n'existe pas dans le serveur
                for (Mark localMark : marksLocales) {
                    if (!marksDistantes.contains(localMark)) {
                        // La mark local n'existe pas dans serveur, on la televerse
                        Log.d(TAG, "localMark a envoyer  au serveur  returned: " + localMark.getDesignation());

                        uploadMark(localMark).thenAccept(result -> {
                            Log.d(TAG, "result returned: " +result );
                            if(result)
                                uploadPhotoMark(localMark).thenAccept(result1 -> {
                                    if(result1) {
                                        Log.d(TAG, "result1 returned: " +result );
                                        List<String> filesName = getNameFilesPhotos(localMark);
                                        filesName.forEach(filename -> uploadPhotoFileOfMark(filename, localMark.getMarkID()));
                                    }
                                }).exceptionally(ex1 -> {
                                    runOnUiThread(() -> {
                                        Toast.makeText(getApplicationContext(), "Erreur : " + ex1.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                    return null;
                                });
                        }).exceptionally(ex -> {
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Erreur : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                            return null;
                        });

                    }
                }
            }

    }

    public CompletableFuture<Boolean> uploadMark(Mark localMark) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        String jsonString = gson.toJson(localMark);

        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/upload/mark")
                .put(body)
                .build();

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    future.complete("OK".equals(jsonResponse));
                } else {
                    future.complete(false);
                }
            }
        });

        return future;
    }

    public CompletableFuture<Boolean> uploadPhotoMark(Mark localMark) {
        OkHttpClient okHttpClient = new OkHttpClient();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Gson gson = new Gson();
        List<Photo> photos = mDatabase.photoDao().getAllPhotosMark(localMark.getMarkID());
        if (!photos.isEmpty()) {
            String jsonString = gson.toJson(photos);

            RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));


            Request request = new Request.Builder().url("http://" + MainActivity.ip + ":3000/mobile/upload/photo")
                    .put(body)
                    .build();



            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        future.complete("OK".equals(jsonResponse));
                    } else {
                        future.complete(false);
                    }
                }
            });


        } else future.complete(false);

        return future;
    }

    private void deleteRemoteMarkWithPhotos(Mark markDist) {
        OkHttpClient okHttpClient = new OkHttpClient();


        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/delete/mark/"+chantierID+"/"+markDist.getMarkID())
                .delete()
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

    private void deleteLocalMarkWithPhotos(Mark localMark) {
        List<String> fichiersPhotos ;
        fichiersPhotos = getNameFilesPhotos(localMark);
        mDatabase.markDao().delete(localMark);
        File appDir = getFilesDir();
        for(String fichier:fichiersPhotos){
            File file = new File(appDir, nomEntreprise + "/" +nomChantier + "/" +typeLot + "/" +"photos/" + localMark.getMarkID() + "/" + fichier);
            if (file.exists()) {
                file.delete();
            }
        }


    }

    private void getDistMarkWithPhotos(Mark markDist) {

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/getMark/withPhoto/" + markDist.getMarkID())
                .build();
        OkHttpClient okHttpClient =new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                    runOnUiThread(Toast.makeText(Zoom.this, "Server error", Toast.LENGTH_SHORT)::show);
                    return;
                }

                String jsonResponse = response.body().string();
                Log.d("resultatbody", jsonResponse);

                final GsonBuilder builder = new GsonBuilder();
                final Gson gson = builder.create();


                // Désérialiser result1 en une liste d'objets User
                Type MarkListType = new TypeToken<List<Mark>>() {
                }.getType();
                List<Mark> marks = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("marks"), MarkListType);

                Type PhotoListType = new TypeToken<List<Photo>>() {
                }.getType();
                List<Photo> photos = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("photos"), PhotoListType);

                mDatabase.markDao().insertAll(marks);
                mDatabase.photoDao().insertAll(photos);
                List<String> photosNameFile=getNameFilesPhotos(markDist);
                photosNameFile.forEach(fileName->{
                    downloadPhotoFileOfMark(fileName,markDist.getMarkID());
                });

                runOnUiThread(() -> {
                    Toast.makeText(Zoom.this, "mark récuperée", Toast.LENGTH_SHORT).show();

                });
            }
        });

    }

    private void getDistPhotos(Mark markDist) {

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/mobile/getMark/withPhoto/" + markDist.getMarkID())
                .build();
        OkHttpClient okHttpClient =new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                    runOnUiThread(Toast.makeText(Zoom.this, "Server error", Toast.LENGTH_SHORT)::show);
                    return;
                }

                String jsonResponse = response.body().string();
                Log.d("resultatbody", jsonResponse);

                final GsonBuilder builder = new GsonBuilder();
                final Gson gson = builder.create();


                // Désérialiser result1 en une liste d'objets User
                Type MarkListType = new TypeToken<List<Mark>>() {
                }.getType();
                List<Mark> marks = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("marks"), MarkListType);

                Type PhotoListType = new TypeToken<List<Photo>>() {
                }.getType();
                List<Photo> photos = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("photos"), PhotoListType);

                //mDatabase.markDao().insertAll(marks);
                mDatabase.photoDao().insertAll(photos);
                List<String> photosNameFile=getNameFilesPhotos(markDist);
                photosNameFile.forEach(fileName->{
                    downloadPhotoFileOfMark(fileName,markDist.getMarkID());
                });

                runOnUiThread(() -> {
                    Toast.makeText(Zoom.this, "mark récuperée", Toast.LENGTH_SHORT).show();

                });
            }
        });

    }

    private List<String> getNameFilesPhotos(Mark localMark) {

        return mDatabase.photoDao().getAllNameFilesOfMark(localMark.getMarkID());


    }

    private void verifierMarksCommunes(List<Mark> marksLocales, List<Mark> marksDistantes) {
        for (Mark localMark : marksLocales) {
            if (marksDistantes.contains(localMark)) {
                Mark markDist=getMark(marksDistantes,localMark);
                if(localMark.getStatut().equals("SNT") && !markDist.getStatut().equals("SNT")){
                    updateLocalMark(markDist);


                }
                else if(!localMark.getStatut().equals("SNT") && markDist.getStatut().equals("SNT")){
                    updateDistMark(localMark);


                }
                else if(localMark.getStatut().equals("TNV") && markDist.getStatut().equals("TV")){
                    updateLocalMark(markDist);


                }
                else if(localMark.getStatut().equals("TV") && markDist.getStatut().equals("TNV")){
                    updateDistMark(localMark);


                }
                updateLocalPhotoMark(markDist);
                updateDistPhotoMark(localMark);
            }
        }


    }
//todo
    private void updateDistPhotoMark(Mark mark) {
        if(typeEntreprise.equals("client")||typeEntreprise.equals("ES")){
                    List<String> filesName = getNameFilesPhotos(mark);
                    filesName.forEach(filename -> {uploadPhotoMark(mark);uploadPhotoFileOfMark(filename, mark.getMarkID());});
                }

    }
//todo
    private void updateLocalPhotoMark(Mark mark) {
        if(typeEntreprise.equals("ER")){
            List<Photo> photos = new ArrayList<>(mDatabase.photoDao().getAllPhotosMark(mark.getMarkID()));
            if(photos.isEmpty()) getDistPhotos(mark);

        }
    }

    private void updateDistMark(Mark mark) {

        OkHttpClient client = new OkHttpClient();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("statut", mark.getStatut())
                .addFormDataPart("markID", mark.getMarkID())
                .build();
        Log.d(TAG, "updateDistMark() called with: mark = [" + mark.getMarkID() +" "+ mark.getStatut() +"]");
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


                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "statut modifié", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "no synchro", Toast.LENGTH_SHORT).show());
                }
            }
        });

    }

    private void updateLocalMark(Mark mark) {

        mDatabase.markDao().update(mark);



    }

    private Mark getMark(List<Mark> marksListe, Mark marc) {

        for(Mark mark:marksListe){
            if(mark.getMarkID().equals(marc.getMarkID())){
                return mark;
            }
        }

        return null;
    }

    protected CompletableFuture<Boolean> downloadFileAsync(Context context, OkHttpClient clientHttp, String url, String file) {
        return downloadFile(context, clientHttp, url, file, typeLot, nomClient, nomChantier, nomEntreprise);
    }

    public static CompletableFuture<Boolean> downloadFile(Context context, OkHttpClient client, String url, String fileName, String type, String nomClient, String nomChantier, String nomEntreprise) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(url + "/" + nomClient + "/" + nomChantier + "/" + type + "/" + fileName)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Download", "Download failed: " + e.getMessage());
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show());
                }
                result.complete(false);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        Log.e("Download", "Server returned error: " + response.code());
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(false);
                        return;
                    }

                    File appDir = context.getFilesDir();
                    File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + type);

                    if (!subDir.exists() && !subDir.mkdirs()) {
                        Log.e("Download", "Failed to create directory");
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(false);
                        return;
                    }

                    File file = new File(subDir, fileName);
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(true);
                    } catch (IOException e) {
                        Log.e("Download", "Error saving file: " + e.getMessage());
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Error saving file", Toast.LENGTH_SHORT).show());
                        }
                        result.complete(false);
                    }
                } catch (Exception e) {
                    Log.e("Download", "Unexpected error: " + e.getMessage(), e);
                    result.complete(false);
                }
            }
        });

        return result;
    }

    public void handlePdfFile() {
        File appDir = getFilesDir();
        File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + typeLot);
        File file = new File(subDir, selectedPlan.getFile());

        // Initialisation du ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Téléchargement en cours...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (file.exists()) {
//            runOnUiThread(() -> {
//
//                Toast.makeText(this, "Fichier local trouvé", Toast.LENGTH_SHORT).show();
//            });
            progressDialog.dismiss(); // Fermer le ProgressDialog si le fichier est localement disponible
            if (displayPdf(file, selectedPlan.getPlanID())) {
                drawAllMarks(marks);
            }
        } else {
            CompletableFuture<Boolean> downloadFuture = downloadFileAsync(
                    this,
                    new OkHttpClient(),
                    "http://" + MainActivity.ip + ":3000/download",
                    selectedPlan.getFile()
            );

            downloadFuture.thenAccept(success -> {
                runOnUiThread(() -> {
                    progressDialog.dismiss(); // Fermer le ProgressDialog après le téléchargement
                    if (success) {
                        File fileN = new File(subDir, selectedPlan.getFile());
                        if (displayPdf(fileN, selectedPlan.getPlanID())) {
                            drawAllMarks(marks);
                        }
                    } else {
                        Toast.makeText(this, "Téléchargement échoué", Toast.LENGTH_SHORT).show();
                    }
                });
            }).exceptionally(ex -> {
                runOnUiThread(() -> {
                    progressDialog.dismiss(); // Fermer le ProgressDialog en cas d'exception
                    Toast.makeText(this, "Erreur : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
                return null;
            });
        }
    }

    private boolean displayPdf(File file, int planID) {
        if (!file.exists()) {
            Log.e("displayPdf", "Le fichier n'existe pas : " + file.getAbsolutePath());
            return false;
        }

        try (ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
             PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor)) {

            PdfRenderer.Page page = pdfRenderer.openPage(0);
            pagex = page.getWidth();
            pagey = page.getHeight();
            bitmap = Bitmap.createBitmap(pagex, pagey, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            runOnUiThread(() -> {
                ((ConstraintLayout)findViewById(R.id.vue_principale)).setBackground(null);
                imageView1.setImageBitmap(bitmap);
                imageView1.setId(planID);

                    dx = (float) imageView1.getWidth() / 2 - (float) bitmap.getWidth() / 2;
                    dy = (float) imageView1.getHeight() / 2 - (float) bitmap.getHeight() / 2;
                    scaleFactor = (float) imageView1.getWidth() / (float) bitmap.getWidth();
                    matrix.reset();
                    matrix.postTranslate(dx, dy);
                matrix.setScale(scaleFactor, scaleFactor);


                imageView1.setImageMatrix(matrix);
                layout.invalidate();

            });

            page.close();

            return true;

        } catch (IOException e) {
            Log.e("displayPdf", "Erreur lors de l'affichage du PDF : " + e.getMessage(), e);
            return false;
        }
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
        Mark mark = new Mark("mark_" + (int) posX + "_" + (int) posY+"_"+imageView1.getId(), valeurs[0], valeurs[3], valeurs[6], (int) posX, (int) posY, valeurs[1], valeurs[2], valeurs[5], valeurs[4], imageView1.getId(),entrepriseID);
        addMarker(mark);
        // traitement de creation d'une mark
        }


    }

    public void afficherMarksSynchro(List<Mark> listeMarks) {

        runOnUiThread(() -> {
            markViews.forEach(markview -> layout.removeView(markview.getImageButton()));
            markViews.clear();
            marks.clear();
            marks.addAll(listeMarks);
            drawAllMarks(marks);
            layout.invalidate();


        });




    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 6.5f));
//            matrix.setScale(scaleFactor, scaleFactor);
//            //imageView1.setImageMatrix(matrix);
//            float  dx= (float) imageView1.getWidth() /2- (float)bitmap.getWidth()/2* scaleFactor;
//            float  dy= (float) imageView1.getHeight() /2- (float) bitmap.getHeight() /2* scaleFactor;
//            matrix.postTranslate(dx, dy);
//            imageView1.setImageMatrix(matrix);
//            markViews.forEach(Zoom.this::updateImageViewMarkPosition);
//            return false;
//        }
//    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Calculer le facteur de zoom relatif
            float scaleFactor = detector.getScaleFactor();
            float previousScaleFactor = Zoom.this.scaleFactor;
            Zoom.this.scaleFactor *= scaleFactor;
            Zoom.this.scaleFactor = Math.max(1.0f, Math.min(Zoom.this.scaleFactor, 6.5f));

            // Obtenir le foyer du zoom (point entre les deux doigts)
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            // Appliquer le zoom en gardant le foyer du zoom fixe
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

            // Mettre à jour l'imageView avec la nouvelle matrice
            imageView1.setImageMatrix(matrix);

            // Mettre à jour les marques (si nécessaire)
            markViews.forEach(Zoom.this::updateImageViewMarkPosition);

            return true; // Indique que l'événement a été géré
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Gérer le double clic ici
            handleDoubleClick(e);
            return true; //super.onDoubleTap(e);
        }

    }

    private void handleDoubleClick(MotionEvent e) {
    if(selectedPlan.getPlanID()!=-1)   afficherFenetreMark(null,e);

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
                    icone.setImageResource(R.drawable.icons8_taache_50);
                    break;
                case "note":
                    icone.setImageResource(R.drawable.icons8_note_50);
                    break;
                default:
                    // Ajoutez un cas par défaut pour gérer les types inconnus
                    Log.e("Zoom", "Type de marque inconnu: " + mark.getType());
                    break;
            }
            icone.setContentDescription(mark.getMarkID());
            if(mark.getStatut().equals("SNT"))icone.setColorFilter(Color.RED);
            else if (mark.getStatut().equals("TNV"))icone.setColorFilter(Color.BLUE);
            else icone.setColorFilter(Color.GREEN);
            // Ajoutez l'imageView au layout


            // Assurez-vous que layout n'est pas nul et est bien un FrameLayout
            if (layout != null) {
                layout.addView(icone);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) icone.getLayoutParams();
                params.width = 60;
                params.height = 60;
                params.leftMargin = mark.getPosx();
                params.topMargin = mark.getPosy();
                icone.setLayoutParams(params);

                //traitement de l'icone mark
                MarkView markView=new MarkView(mark, icone);
                markViews.add(markView);
                icone.setOnClickListener(view -> {
                    //MarkView mv= markViews.stream().filter(markView -> markView.getImageButton().equals(view)).findFirst().orElse(null);
                    afficherFenetreMark(markView,null);
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
        MyBottomSheetDialogFragment bottomSheet = new MyBottomSheetDialogFragment(mv, e,this, entrepriseID, nomEntreprise,typeEntreprise,chantierID,nomClient,nomChantier,typeLot);
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

    private void drawAllMarks(List<Mark> marks) {
        marks.forEach(this::drawMark);
        //isBottomSheetOpen=false;
    }

    protected   MarkView findMarker(String markID){
        return markViews.stream().filter(markView -> markView.getMark().getMarkID().equals(markID)).findFirst().orElse(null);
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //startDownload();
//            } else {
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        adapterPlans = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plans);
        adapterPlans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        MenuItem menuItem = menu.findItem(R.id.spinner_item);
        spinnerPlans = (Spinner) menuItem.getActionView();
        spinnerPlans.setAdapter(adapterPlans);

        spinnerPlans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isBottomSheetOpen) {
                    spinnerPlans.setEnabled(false);
                    return;
                }
                spinnerPlans.setEnabled(true);
                selectedPlan = (Plan) parent.getItemAtPosition(position);
                if (selectedPlan.getPlanID() != -1) {
                    markViews.forEach(markview -> layout.removeView(markview.getImageButton()));
                    markViews.clear();
                    marks.clear();
                    //init=true;
                    marks = getAllMarksLocales(selectedPlan.getPlanID());
                    scaleFactor = 1.0f;
                    matrix = new Matrix();
                    imageView1.setImageMatrix(matrix);
                    handlePdfFile();
                }
                else {
                    markViews.forEach(markview -> layout.removeView(markview.getImageButton()));
                    markViews.clear();
                    marks.clear();
                    imageView1.setImageBitmap(null);
                    Drawable drawable = ContextCompat.getDrawable(Zoom.this, R.drawable.affichage_2_100);
                    ((ConstraintLayout) findViewById(R.id.vue_principale)).setBackground(drawable);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_synch_plan) {
            afficherDialogueConfirmationPlan();

            return true;
        }
        if (id == R.id.filtre) {
            if(selectedPlan.getPlanID()!=-1)afficherFenetrefiltre(selectedPlan);
            return true;
        }
        else if (id == R.id.action_synch_mark) {
            if(selectedPlan.getPlanID()!=-1)afficherDialogueConfirmationMark();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public   void afficherFenetrefiltre(Plan  plan) {


            MyBottomSheetDialogFragmentFilter bottomSheetDialogFragment = MyBottomSheetDialogFragmentFilter.newInstance(plan.getPlanID(), typeLot);

            bottomSheetDialogFragment.show(getSupportFragmentManager(), "BottomSheet");




    }

    public void onBottomSheetDismissed(List<Mark> marksAffiches) {

        afficherMarksSynchro(marksAffiches);

    }

    private void afficherDialogueConfirmationPlan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Synchronisation des Plans");
        builder.setMessage("les plans locaux vont être remplacés ?");

        // Bouton Oui
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Zoom.this, "Action confirmée", Toast.LENGTH_SHORT).show();
                synchroniserPlans();
            }
        });

        // Bouton Non
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Zoom.this, "Action annulée", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Fermer le dialogue
            }
        });

        // Créer et afficher le dialogue
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void afficherDialogueConfirmationMark() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Synchronisation des marques");
        builder.setMessage("les marques du plan affiché vont être synchronisées avec le serveur ?");

        // Bouton Oui
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Zoom.this, "Action confirmée", Toast.LENGTH_SHORT).show();
                synchroniserMarks(selectedPlan, listeMarks->afficherMarksSynchro(listeMarks));
            }
        });

        // Bouton Non
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Zoom.this, "Action annulée", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Fermer le dialogue
            }
        });

        // Créer et afficher le dialogue
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteLocalFile(String fileName, String markID) {
        File appDir = getFilesDir();
        File file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + markID + "/" + fileName);
        if (file.exists()) {
            file.delete();
            deleteLocalyFilefromDB(fileName);
        }
        file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + markID);
        if (file.exists() && file.isDirectory() && file.listFiles().length == 0) file.delete();
    }

    private void deleteLocalyFilefromDB(String fileName) {
        mDatabase.photoDao().deletePhoto(fileName);

    }

    private List<String> getLocalFiles(String markID) {
        File appDir = getFilesDir();
        File directory = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + markID + "/");
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    private void downloadPhotoFileOfMark(String fileName, String markID) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/download/" +nomClient+"/"+nomChantier+"/photo/"+ fileName)
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

                    saveFileLocally(response.body().byteStream(), fileName,markID);

                }
            }
        });
    }

//    private void saveFilelocalyToDB(String fileName, String markID) {
//        int currentdate = (int) new Date().getTime();
//        Photo photo = new Photo(null, fileName, String.valueOf(currentdate), markID, entrepriseID);
//        mDatabase.photoDao().insert(photo);
//
//    }


//    private void synchroniserPhotosByMark(Mark mark) {
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://" + MainActivity.ip + ":3000/download/" + mark.getMarkID())
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//
//
//                    String jsonResponse = response.body().string();
//
//                    // Utiliser Gson pour convertir la réponse JSON en une liste
//                    Gson gson = new Gson();
//                    Type listType = new TypeToken<List<String>>() {
//                    }.getType();
//                    List<String> serverFiles = gson.fromJson(jsonResponse, listType);
//
//                    // Liste des fichiers présents localement sur le client
//                    List<String> localFiles = getLocalFiles(mark.getMarkID());
//
//                    // Parcourir les fichiers du serveur
//                    if (typeEntreprise.equals("ER")) {
//                        for (int i = 0; i < serverFiles.size(); i++) {
//                            String serverFile = serverFiles.get(i);
//
//                            // Si le fichier du serveur n'est pas présent localement, on le télécharge
//                            if (!localFiles.contains(serverFile)) {
//                                downloadPhotoFileOfMark(serverFile,mark.getMarkID());
//                            }
//                        }
//
//                        // Vérifier si des fichiers locaux ont été supprimés du serveur on les supprime localement
//                        for (String localFile : localFiles) {
//                            if (!serverFiles.toString().contains(localFile)) {
//                                // Le fichier a été supprimé du serveur, on le supprime localement
//                                deleteLocalFile(localFile,mark.getMarkID());
//                            }
//                        }
//                    } else if (typeEntreprise.equals("ES") || typeEntreprise.equals("client")) {
//                        for (int i = 0; i < serverFiles.size(); i++) {
//                            String serverFile = serverFiles.get(i);
//
//                            // Si le fichier du serveur n'est pas présent localement, on le supprime
//                            if (!localFiles.contains(serverFile)) {
//                                deleteRemoteFile(serverFile);
//                            }
//                        }
//
//                        // Vérifier si des fichiers locaux ont été supprimés du serveur
//                        for (String localFile : localFiles) {
//                            if (!serverFiles.toString().contains(localFile)) {
//                                // Le fichier local a été supprimé du serveur, on le televerse
//                                uploadFile(localFile,mark.getMarkID());
//                            }
//                        }
//                    }
//
//
//                }
//
//            }
//        });
//
//    }

//    private void deleteRemoteFile(String remoteFile) {
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("http://" + MainActivity.ip + ":3000/upload/delete/photo/" + chantierID + "/" + remoteFile)
//                .delete()
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(Zoom.this, "Échec de l'upload", Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Fichier supprimé avec succès", Toast.LENGTH_SHORT).show());
//                } else {
//                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Erreur lors de l'upload", Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//
//    }

    private void uploadPhotoFileOfMark(String fileName, String markID) {
        OkHttpClient client = new OkHttpClient();
        File appDir = getFilesDir();
        File file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + markID + "/" + fileName);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("markID", markID)
                .build();

        Request request = new Request.Builder()
                .url("http://" + MainActivity.ip + ":3000/upload/upload/photoFile/photo/" + chantierID)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Zoom.this, "Échec de l'upload", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(Zoom.this, "Fichier uploadé avec succès", Toast.LENGTH_SHORT).show());
                } else {
                   runOnUiThread(() -> Toast.makeText(Zoom.this, "Erreur lors de l'upload", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void saveFileLocally(InputStream inputStream, String fileName,String markID) {
        try {
            File appDir = getFilesDir();
            File file = new File(appDir, nomEntreprise + "/" + nomChantier + "/photos/" + markID + "/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            } catch (IOException e) {
            e.printStackTrace();
        }
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


}

