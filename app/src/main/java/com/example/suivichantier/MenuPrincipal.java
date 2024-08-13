package com.example.suivichantier;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity {


    private int entrepriseID ;
    private String nomEntreprise ;
    private String typeEntreprise ;
    private String nomChantier ;
    private int chantierID ;

    protected int lotID ;
    protected  Intent intent;
    private AppDatabase mDatabase;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.menu_principal);
             intent = getIntent();
             entrepriseID = intent.getIntExtra("entrepriseID",0);
             nomEntreprise = intent.getStringExtra("nomEntreprise");
             typeEntreprise = intent.getStringExtra("typeEntreprise");
             nomChantier = intent.getStringExtra("nomChantier");
             chantierID = intent.getIntExtra("chantierID",0);
             mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();


            List<Lot> listeLots;
            if(typeEntreprise.equals("client")) listeLots =mDatabase.lotDao().getAllLotChantier(chantierID);
            else if(typeEntreprise.equals("ES")) listeLots =mDatabase.lotDao().getAllLotChantierES(entrepriseID,chantierID);
            else listeLots =mDatabase.lotDao().getAllLotChantierER(entrepriseID,chantierID);

            Button infrastructureButton = findViewById(R.id.infrastructure_button);
            infrastructureButton.setEnabled(false);
            Button superstructureButton = findViewById(R.id.superstructure_button);
            superstructureButton.setEnabled(false);
            Button cesButton = findViewById(R.id.ces_button);
            cesButton.setEnabled(false);
            Button vrdButton = findViewById(R.id.vrd_button);
            vrdButton.setEnabled(false);
            Button plansExecutionsButton = findViewById(R.id.plans_executions_button);
            Button pvButton = findViewById(R.id.pv_button);
            Button comButton = findViewById(R.id.com_button);
            Button reservesButton = findViewById(R.id.reserves_button);
            Button tachesButton = findViewById(R.id.taches_button);
            Button notesButton = findViewById(R.id.notes_button);

            listeLots.forEach(lot->{
                switch(lot.getDescription()) {
                    case "INFRA":
                        infrastructureButton.setContentDescription(Integer.toString(lot.getLotID()));
                        infrastructureButton.setEnabled(true);break;
                    case "SUPER":
                        superstructureButton.setContentDescription(Integer.toString(lot.getLotID()));
                        superstructureButton.setEnabled(true);break;
                    case "CES":
                        cesButton.setContentDescription(Integer.toString(lot.getLotID()));
                        cesButton.setEnabled(true);break;
                    case "VRD":
                        vrdButton.setContentDescription(Integer.toString(lot.getLotID()));
                        vrdButton.setEnabled(true);break;
                    default:

                }
            });

            plansExecutionsButton.setContentDescription(Integer.toString(chantierID));
            comButton.setContentDescription(Integer.toString(chantierID));
            pvButton.setContentDescription(Integer.toString(chantierID));

            reservesButton.setContentDescription(Integer.toString(chantierID));
            tachesButton.setContentDescription(Integer.toString(chantierID));
            notesButton.setContentDescription(Integer.toString(chantierID));

            intent = new Intent(MenuPrincipal.this, Zoom.class);
            intent.putExtra("nomEntreprise", nomEntreprise);
            intent.putExtra("entrepriseID", entrepriseID);
            intent.putExtra("typeEntreprise", typeEntreprise);
            intent.putExtra("nomChantier", nomChantier);
            intent.putExtra("chantierID", chantierID);


            infrastructureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String typeLot="INFRA";
                    lotID = Integer.parseInt((String) v.getContentDescription());
                    intent.putExtra("lotID", lotID);
                    intent.putExtra("typeLot", typeLot);
                    startActivity(intent);
                }

            });



            superstructureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String typeLot="SUPER";
                    lotID = Integer.parseInt((String) v.getContentDescription());
                    intent.putExtra("lotID", lotID);
                    intent.putExtra("typeLot", typeLot);
                    startActivity(intent);
                }
            });


            cesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String typeLot="CES";
                    lotID = Integer.parseInt((String) v.getContentDescription());
                    intent.putExtra("lotID", lotID);
                    intent.putExtra("typeLot", typeLot);
                    startActivity(intent);
                }
            });

            vrdButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String typeLot="VRD";
                    lotID = Integer.parseInt((String) v.getContentDescription());
                    intent.putExtra("lotID", lotID);
                    intent.putExtra("typeLot", typeLot);
                    startActivity(intent);
                }
            });

            reservesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MenuPrincipal.this, AnnexeReserves.class);
                    intent1.putExtra("nomEntreprise", nomEntreprise);
                    intent1.putExtra("entrepriseID", entrepriseID);
                    intent1.putExtra("typeEntreprise", typeEntreprise);
                    intent1.putExtra("nomChantier", nomChantier);
                    intent1.putExtra("chantierID", chantierID);
                    startActivity(intent1);
                }
            });

            tachesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MenuPrincipal.this, AnnexeTaches.class);
                    intent1.putExtra("nomEntreprise", nomEntreprise);
                    intent1.putExtra("entrepriseID", entrepriseID);
                    intent1.putExtra("typeEntreprise", typeEntreprise);
                    intent1.putExtra("nomChantier", nomChantier);
                    intent1.putExtra("chantierID", chantierID);
                    startActivity(intent1);
                }
            });

            notesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MenuPrincipal.this, AnnexeNotes.class);
                    intent1.putExtra("nomEntreprise", nomEntreprise);
                    intent1.putExtra("entrepriseID", entrepriseID);
                    intent1.putExtra("typeEntreprise", typeEntreprise);
                    intent1.putExtra("nomChantier", nomChantier);
                    intent1.putExtra("chantierID", chantierID);
                    startActivity(intent1);
                }
            });

            pvButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MenuPrincipal.this, ListPV.class);
                    intent1.putExtra("nomEntreprise", nomEntreprise);
                    intent1.putExtra("entrepriseID", entrepriseID);
                    intent1.putExtra("typeEntreprise", typeEntreprise);
                    intent1.putExtra("nomChantier", nomChantier);
                    intent1.putExtra("chantierID", chantierID);
                    startActivity(intent1);
                }
            });

            comButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MenuPrincipal.this, ListCom.class);
                    intent1.putExtra("nomEntreprise", nomEntreprise);
                    intent1.putExtra("entrepriseID", entrepriseID);
                    intent1.putExtra("typeEntreprise", typeEntreprise);
                    intent1.putExtra("nomChantier", nomChantier);
                    intent1.putExtra("chantierID", chantierID);
                    startActivity(intent1);
                }
            });
            plansExecutionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent1 = new Intent(MenuPrincipal.this, ListePlanExecution.class);
                    intent1.putExtra("nomEntreprise", nomEntreprise);
                    intent1.putExtra("entrepriseID", entrepriseID);
                    intent1.putExtra("typeEntreprise", typeEntreprise);
                    intent1.putExtra("nomChantier", nomChantier);
                    intent1.putExtra("chantierID", chantierID);
                    startActivity(intent1);
                }
            });

        }
    private int getLotID(String typeLot){

        List<Lot> lot=null;
        switch(typeEntreprise){
            case "client": lot=mDatabase.lotDao().getLot(chantierID,typeLot);break;
            case "ES": lot=mDatabase.lotDao().getLotES(entrepriseID,chantierID,typeLot);break;
            case "ER": lot=mDatabase.lotDao().getLotER(entrepriseID,chantierID,typeLot);break;
        }

        if (!lot.isEmpty())return lot.get(0).getLotID();
        else return 0;
    }

 }


