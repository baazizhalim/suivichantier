package com.example.suivichantier;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.room.Room;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    static String ip="192.168.1.2";
    private AppDatabase mDatabase;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Entreprise user;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //MyApp app = (MyApp) getApplication();
        //mDatabase = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "my-database").build();
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").allowMainThreadQueries().build();
        //mDatabase = app.getDatabase();
        okHttpClient = new OkHttpClient();
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                user = mDatabase.entrepriseDao().login(username, password);
                if (user != null) {
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    // Rediriger vers une autre activité
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
        Request request = new Request.Builder().url("http://" + MainActivity.ip + ":5000/verifierUser")
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NotNull Call call,
                    @NotNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Problème avec le serveur", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (!responseBody.equals("0")) {
                    getActivity(getApplicationContext()).runOnUiThread(() -> {

                        Entreprise user=null;//a creer a partir de responseBody

                        mDatabase.entrepriseDao().insert(user);
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // Rediriger vers une autre activité
                        });
                } else Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();


            }
        });

    }
}