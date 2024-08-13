package com.example.suivichantier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Bienvenue extends AppCompatActivity {
    private OkHttpClient okHttpClient;
    private AppDatabase mDatabase;
    private ProgressDialog progressDialog;
    private ExecutorService executorService;
    private Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bienvenue);
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        okHttpClient = new OkHttpClient();
        Intent intent = getIntent();
        int entrepriseID = intent.getIntExtra("entrepriseID",0);
        String nomEntreprise = intent.getStringExtra("nomEntreprise");
        String typeEntreprise = intent.getStringExtra("typeEntreprise");

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Afficher le message dans un TextView
        TextView textView = findViewById(R.id.nom);
        textView.setText("BIENVENUE " + nomEntreprise+" "+typeEntreprise);

        Button synchro = findViewById(R.id.synchro);
        Button suivi = findViewById(R.id.suivi);

        synchro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();
                executorService.execute(this::recupereDateBase);

            }

            private void recupereDateBase() {
                Bienvenue.this.recupereDateBase(entrepriseID, typeEntreprise);

            }
        });

        suivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Bienvenue.this, ListeChantier.class);
                intent.putExtra("nomEntreprise", nomEntreprise);
                intent.putExtra("entrepriseID", entrepriseID);
                intent.putExtra("typeEntreprise", typeEntreprise);
                startActivity(intent);

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
    private void recupereDateBase(int entrepriseID, String type_entreprise) {


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("entrepriseID", entrepriseID);
            jsonBody.put("type_entreprise", type_entreprise);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Créer une requête HTTP POST avec le corps JSON
        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));


        // while building request
        // we give our form
        // as a parameter to post()
        Request request = new Request.Builder().url("http://" + MainActivity.ip + ":3000/mobile/synchro/")
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NotNull Call call,
                    @NotNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Problème avec le serveur " + e.getMessage(), Toast.LENGTH_SHORT).show());
                handler.post(Bienvenue.this::hideProgressDialog);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("resultatbody", jsonResponse);

                    final GsonBuilder builder = new GsonBuilder();
                    final Gson gson = builder.create();


                    // Désérialiser result1 en une liste d'objets User
                    Type EntrepriseListType = new TypeToken<List<Entreprise>>() {
                    }.getType();
                    List<Entreprise> entreprise = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("entreprise"), EntrepriseListType);

                    Type chantierListType = new TypeToken<List<Chantier>>() {
                    }.getType();
                    List<Chantier> chantier = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("chantier"), chantierListType);

                    // Désérialiser result2 en une liste d'objets Product
                    Type lotListType = new TypeToken<List<Lot>>() {
                    }.getType();
                    List<Lot> lot = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("lot"), lotListType);

                    Type planListType = new TypeToken<List<Plan>>() {
                    }.getType();
                    List<Plan> plan = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("plan"), planListType);

                    Type markListType = new TypeToken<List<Mark>>() {
                    }.getType();
                    List<Mark> mark = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("mark"), markListType);


                    Type remarqueListType = new TypeToken<List<Remarque>>() {
                    }.getType();
                    List<Remarque> remarque = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("remarque"), remarqueListType);

                    Type photoListType = new TypeToken<List<Photo>>() {
                    }.getType();
                    List<Photo> photo = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("photo"), photoListType);

                    Type pvListType = new TypeToken<List<Pv>>() {
                    }.getType();
                    List<Pv> pv = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("pv"), pvListType);

                    Type comminiqueListType = new TypeToken<List<Communique>>() {
                    }.getType();
                    List<Communique> communique = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("communique"), comminiqueListType);


                    Type planExecutionListType = new TypeToken<List<PlanExecution>>() {
                    }.getType();
                    List<PlanExecution> planExecution = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("planExecution"), planExecutionListType);

                    //mDatabase mettre a jour database;
                    mDatabase.runInTransaction(() -> {
                        mDatabase.entrepriseDao().insertAll(entreprise);
                        mDatabase.chantierDao().insertAll(chantier);
                        mDatabase.lotDao().insertAll(lot);
                        mDatabase.planDao().insertAll(plan);
                        mDatabase.markDao().insertAll(mark);
                        mDatabase.photoDao().insertAll(photo);
                        mDatabase.remarqueDao().insertAll(remarque);
                        mDatabase.planExecutionDao().insertAll(planExecution);
                        mDatabase.pvDao().insertAll(pv);
                        mDatabase.communiqueDao().insertAll(communique);

                        });
                    runOnUiThread(() -> {
                        Toast.makeText(Bienvenue.this, "Shyncronisation successful ", Toast.LENGTH_SHORT).show();
                        handler.post(Bienvenue.this::hideProgressDialog);
                    });


                } else
                    runOnUiThread(() -> {
                        Toast.makeText(Bienvenue.this, "Erreur de réponse: " + response.code(), Toast.LENGTH_SHORT).show();
                        handler.post(Bienvenue.this::hideProgressDialog);
                    });


            }

        });

    }


}
