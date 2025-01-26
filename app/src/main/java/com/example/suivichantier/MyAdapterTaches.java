package com.example.suivichantier;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class MyAdapterTaches extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final int entrepriseID ;
    private final int chantierID ;
    private final String nomEntreprise ;
    private final String nomChantier ;
    private final String nomClient ;
    private final String typeEntreprise ;
    private final List<Mark> marks;
    //private Plan plan ;
    //private Lot lot;
    private final AppDatabase mDatabase;


    public MyAdapterTaches(Context context, List<Mark> marks, int entrepriseID , String nomEntreprise , String typeEntreprise, String nomClient, String nomChantier , int chantierID) {
        this.context = context;
        this.marks = marks;
        this.entrepriseID = entrepriseID;
        this.chantierID = chantierID;
        this.nomEntreprise = nomEntreprise;
        this.nomChantier = nomChantier;
        this.nomClient = nomClient;
        this.typeEntreprise = typeEntreprise;
        mDatabase = Room.databaseBuilder(context, AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_reserves, parent, false);
            return new MyAdapterReserves.HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserves, parent, false);
            return new MyAdapterReserves.ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyAdapterTaches.HeaderViewHolder) {
            MyAdapterTaches.HeaderViewHolder headerHolder = (MyAdapterTaches.HeaderViewHolder) holder;
            //headerHolder.headerTitle.setText("Liste des Items");
        } else {
            MyAdapterReserves.ItemViewHolder itemHolder = (MyAdapterReserves.ItemViewHolder) holder;
            Mark mark = marks.get(position - 1); // Compensate for header
            itemHolder.itemText0.setText(String.valueOf(position));
            itemHolder.itemText1.setText(mark.getDesignation());
            Plan plan = mDatabase.planDao().getOnePlan(mark.getPlanID());
            Lot lot = mDatabase.lotDao().getLotById(plan.getLotID());
            itemHolder.itemText2.setText(lot.getDescription());
            itemHolder.itemText6.setText(plan.toString());
            itemHolder.itemText3.setText(mark.getDate());
            itemHolder.itemText4.setText(mark.getObservation());
            itemHolder.itemText5.setText(mark.getStatut());

            switch(mark.getStatut()){
                case "SNT":itemHolder.itemText5.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                    break;
                case "TNV":itemHolder.itemText5.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_light));break;
                case "TV":itemHolder.itemText5.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light));break;
            }
            //itemHolder.markID=mark.getMarkID();
            //itemHolder.planID=mark.getPlanID();
            //itemHolder.lotID=plan.getLotID();

            itemHolder.itemText1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gérer l'événement de clic sur le bouton ici
                    // Par exemple, afficher un toast ou effectuer une action

                    Intent intent = new Intent(context, Zoom1.class);
                    intent.putExtra("nomEntreprise", nomEntreprise);
                    intent.putExtra("entrepriseID", entrepriseID);
                    intent.putExtra("typeEntreprise", typeEntreprise);
                    intent.putExtra("nomChantier", nomChantier);
                    intent.putExtra("chantierID", chantierID);
                    intent.putExtra("nomClient", nomClient);
                    intent.putExtra("markID", mark.getMarkID());
                    intent.putExtra("planID",mark.getPlanID() );
                    intent.putExtra("lotID",plan.getLotID());
                    intent.putExtra("lot",mark.getLot() );
                    intent.putExtra("typeLot",plan.getDescription() );
                    intent.putExtra("parentActivity","AnnexeTaches" );
                    startActivity(context,intent,null);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return marks.size() + 1; // Add one for the header
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle0;
        TextView headerTitle1;
        TextView headerTitle2;
        TextView headerTitle3;
        TextView headerTitle4;
        TextView headerTitle5;
        TextView headerTitle6;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle0 = itemView.findViewById(R.id.header_title0);
            headerTitle1 = itemView.findViewById(R.id.header_title1);
            headerTitle2 = itemView.findViewById(R.id.header_title2);
            headerTitle3 = itemView.findViewById(R.id.header_title3);
            headerTitle4 = itemView.findViewById(R.id.header_title4);
            headerTitle5 = itemView.findViewById(R.id.header_title5);
            headerTitle6 = itemView.findViewById(R.id.header_title6);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemText0;
        TextView itemText1;
        TextView itemText2;
        TextView itemText3;
        TextView itemText4;
        TextView itemText5;
        TextView itemText6;
        String markID;
        int planID;
        int LotID;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText0 = itemView.findViewById(R.id.item_text0);
            itemText1 = itemView.findViewById(R.id.item_text1);
            itemText2 = itemView.findViewById(R.id.item_text2);
            itemText3 = itemView.findViewById(R.id.item_text3);
            itemText4 = itemView.findViewById(R.id.item_text4);
            itemText5 = itemView.findViewById(R.id.item_text5);
            itemText6 = itemView.findViewById(R.id.item_text6);
        }
    }
}
