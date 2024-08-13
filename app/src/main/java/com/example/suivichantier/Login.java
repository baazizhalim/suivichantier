package com.example.suivichantier;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity{

        private AppDatabase mDatabase;
        private EditText usernameEditText;
        private EditText passwordEditText;
        private Entreprise user;
        private OkHttpClient okHttpClient;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login);
            mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

            okHttpClient = new OkHttpClient();
            usernameEditText = findViewById(R.id.username);
            passwordEditText = findViewById(R.id.password);
            Button loginButton = findViewById(R.id.login);



            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    user = mDatabase.entrepriseDao().login(username, password);
                    if (user != null) {
                        Toast.makeText(Login.this, "Login successful local", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, Bienvenue.class);
                        intent.putExtra("nomEntreprise", user.getNom());
                        intent.putExtra("entrepriseID", user.getEntrepriseID());
                        intent.putExtra("typeEntreprise", user.getType());
                        startActivity(intent);
                    } else {
                        verifierServeur(username, password);

                    }
                }
            });
        }

        private  void verifierServeur (String username, String password) {
            Entreprise user=null;
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", username);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Créer une requête HTTP POST avec le corps JSON
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));


            // while building request
            // we give our form
            // as a parameter to post()
            Request request = new Request.Builder().url("http://" + MainActivity.ip + ":3000/mobile/verifierUser/")
                    .post(body)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(
                        @NotNull Call call,
                        @NotNull IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Problème avec le serveur "+e.getMessage(), Toast.LENGTH_SHORT).show());
//                getActivity(getApplicationContext()).runOnUiThread(() -> {
//                    AlertDialog.Builder alert=new AlertDialog.Builder(getApplicationContext());
//                    alert.setTitle("info");
//                    alert.setMessage("Problème avec le serveur"+e.getMessage());
//                    alert.setPositiveButton("Oui", null);
//                    alert.show();
//                });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    Log.d("resultatbody",responseBody);
                    if (!"absent".contentEquals(responseBody) ){
                        runOnUiThread(() -> {
                            final GsonBuilder builder = new GsonBuilder();
                            final Gson gson = builder.create();
                            Entreprise user = null;

                            user = gson.fromJson(responseBody, Entreprise.class);
                            if (user != null) {
                                Toast.makeText(Login.this, user.getEntrepriseID()+" " + user.getNom(), Toast.LENGTH_SHORT).show();

                                mDatabase.entrepriseDao().insert(user);
                                Toast.makeText(Login.this, "Login successful distant" + user.getNom(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, Bienvenue.class);
                                intent.putExtra("nomEntreprise", user.getNom());
                                intent.putExtra("entrepriseID", user.getEntrepriseID());
                                intent.putExtra("typeEntreprise", user.getType());
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, "problème d'authentification" + user.getNom(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else
                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();

                        });


                }

            });

        }
    }

