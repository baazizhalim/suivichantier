package com.example.suivichantier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {
    public static String ip="192.168.1.5";
    private AppDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        setContentView(R.layout.activity_main);

        Button connectButton = findViewById(R.id.connect_button);


        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);}
        });



        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userName=sharedPreferences.getString("userToken", "");
            Entreprise user=mDatabase.entrepriseDao().getUserInfo(userName);
            Intent intent = new Intent(this, Bienvenue.class);
            intent.putExtra("nomEntreprise", user.getNom());
            intent.putExtra("entrepriseID", user.getEntrepriseID());
            intent.putExtra("typeEntreprise", user.getType());
            startActivity(intent);
            finish();
        }


    }
}
