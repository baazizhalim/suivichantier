package com.example.suivichantier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AnnexeCom extends AppCompatActivity {
    private int chantierID;
    private String nomClient;
    private String nomChantier;
    private int entrepriseID;
    private String nomEntreprise;
    private String typeEntreprise;
    private RecyclerView recyclerView;

    private AppDatabase mDatabase;
    private List<Communique> com=new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_com);
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

         recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
         chantierID = intent.getIntExtra("chantierID",0);
         nomClient = intent.getStringExtra("nomClient");
         nomChantier = intent.getStringExtra("nomChantier");
         entrepriseID = intent.getIntExtra("entrepriseID",0);
         nomEntreprise = intent.getStringExtra("nomEntreprise");
         typeEntreprise = intent.getStringExtra("typeEntreprise");
        com=mDatabase.communiqueDao().getAllCommuniques(chantierID);

        MyAdapterCom adapter = new MyAdapterCom(this,com,nomClient,nomChantier,chantierID,nomEntreprise,entrepriseID,typeEntreprise);
        recyclerView.setAdapter(adapter);
    }

    protected CompletableFuture<Boolean> downloadFileAsync(Context context, OkHttpClient clientHttp, String url, String file) {
        return downloadFile(context, clientHttp, url, file, "coms", nomClient, nomChantier, nomEntreprise);
    }

    public static CompletableFuture<Boolean> downloadFile(Context context, OkHttpClient client, String url, String fileName, String typeLot, String nomClient, String nomChantier, String nomEntreprise) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(url + "/" + nomClient + "/" + nomChantier + "/" + typeLot + "/" + fileName)
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
                    File subDir = new File(appDir, nomEntreprise + "/" + nomChantier + "/" + typeLot);

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


    private void synchroniserPlans() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Téléchargement en cours...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("chantierID", chantierID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Créer une requête HTTP POST avec le corps JSON
        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));


        Request request = new Request.Builder().url("http://" + MainActivity.ip + ":3000/mobile/synchro/plans/com")
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
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("resultatbody", jsonResponse);

                    final GsonBuilder builder = new GsonBuilder();
                    final Gson gson = builder.create();

                    Type planListType = new TypeToken<List<Communique>>() {
                    }.getType();
                    List<Communique> plansSync = gson.fromJson(gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("COM"), planListType);
                    Log.d("resultatbody", plansSync.toString());

                    //mDatabase mettre a jour database;
                    mDatabase.runInTransaction(() -> {
                        mDatabase.communiqueDao().deleteAll();
                        mDatabase.communiqueDao().insertAll(plansSync);
                        com=mDatabase.communiqueDao().getAllCommuniques(chantierID);
                        runOnUiThread(() -> {

                            com.forEach(plan ->
                                    {
                                        CompletableFuture<Boolean> downloadFuture = downloadFileAsync(
                                                getApplicationContext(),
                                                new OkHttpClient(),
                                                "http://" + MainActivity.ip + ":3000/download",
                                                plan.getFile()
                                        );

                                        downloadFuture.thenAccept(success -> {
                                            runOnUiThread(() -> {
                                                progressDialog.dismiss(); // Fermer le ProgressDialog après le téléchargement
                                                if (success) {

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Téléchargement échoué", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }).exceptionally(ex -> {
                                            runOnUiThread(() -> {
                                                progressDialog.dismiss(); // Fermer le ProgressDialog en cas d'exception
                                                Toast.makeText(getApplicationContext(), "Erreur : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                            return null;
                                        });
                                    }
                            );


                            MyAdapterCom adapter = new MyAdapterCom(AnnexeCom.this,com,nomClient,nomChantier,chantierID,nomEntreprise,entrepriseID,typeEntreprise);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        });

                    });

                } else
                    runOnUiThread(() -> {
                        Toast.makeText(AnnexeCom.this, "Erreur de réponse: " + response.code(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });


            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_1, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_synch) {


            synchroniserPlans();

            return true;
        }


        return super.onOptionsItemSelected(item);
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



