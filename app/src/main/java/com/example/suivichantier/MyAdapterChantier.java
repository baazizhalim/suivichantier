package com.example.suivichantier;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class MyAdapterChantier extends RecyclerView.Adapter<MyAdapterChantier.MyViewHolder> {
    private final int entrepriseID ;
    private final String nomEntreprise ;
    private final String typeEntreprise ;
    private final List<ListItemChantier> itemList;
    private final Context context;
    //private boolean nomClient;
    private AppDatabase mDatabase;

    public MyAdapterChantier(Context context, List<ListItemChantier> itemList, int entrepriseID , String nomEntreprise , String typeEntreprise ) {
        this.entrepriseID = entrepriseID;
        this.nomEntreprise = nomEntreprise;
        this.typeEntreprise = typeEntreprise;
        this.context = context;
        this.itemList = itemList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_chantier, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListItemChantier item = itemList.get(position);
        holder.button.setText(item.getNom());


        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MenuPrincipal.class);
                intent.putExtra("nomEntreprise", nomEntreprise);
                intent.putExtra("entrepriseID", entrepriseID);
                intent.putExtra("typeEntreprise", typeEntreprise);
                intent.putExtra("chantierID", item.getChantierID());
                intent.putExtra("nomChantier", item.getNom());
                //intent.putExtra("nomClient", getNomClient(item.getChantierID()));

                startActivity(context,intent,null);
            }
        });
    }

    private String getNomClient(int chantierID) {
        mDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        return mDatabase.chantierDao().getClientName(chantierID);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        Button button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }


}
