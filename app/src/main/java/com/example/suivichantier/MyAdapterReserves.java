package com.example.suivichantier;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapterReserves extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private int entrepriseID ;
    private String nomEntreprise ;
    private String typeEntreprise ;
    private List<Mark> items;

    public MyAdapterReserves(Context context,List<Mark> items,int entrepriseID , String nomEntreprise , String typeEntreprise ) {
        this.context = context;
        this.items = items;
        this.entrepriseID = entrepriseID;
        this.nomEntreprise = nomEntreprise;
        this.typeEntreprise = typeEntreprise;
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
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserves, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            //headerHolder.headerTitle.setText("Liste des Items");
        } else {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            Mark item = items.get(position - 1); // Compensate for header
            itemHolder.itemText1.setText(item.getDesignation());
            itemHolder.itemText2.setText(String.valueOf(item.getPlanID()));
            itemHolder.itemText3.setText(item.getDate());
            itemHolder.itemText4.setText(item.getObservation());
            itemHolder.itemText5.setText(item.getStatut());
            itemHolder.markID=item.getMarkID();
            itemHolder.planID=item.getPlanID();

            itemHolder.itemText1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gérer l'événement de clic sur le bouton ici
                    // Par exemple, afficher un toast ou effectuer une action

                    Intent intent = new Intent(context, Zoom.class);
                    intent.putExtra("nomEntreprise", nomEntreprise);
                    intent.putExtra("entrepriseID", entrepriseID);
                    intent.putExtra("typeEntreprise", typeEntreprise);
                    intent.putExtra("markID", item.getMarkID());
                    intent.putExtra("planID", item.getPlanID());

                    startActivity(context,intent,null);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1; // Add one for the header
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle1;
        TextView headerTitle2;
        TextView headerTitle3;
        TextView headerTitle4;
        TextView headerTitle5;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle1 = itemView.findViewById(R.id.header_title1);
            headerTitle2 = itemView.findViewById(R.id.header_title2);
            headerTitle3 = itemView.findViewById(R.id.header_title3);
            headerTitle4 = itemView.findViewById(R.id.header_title4);
            headerTitle5 = itemView.findViewById(R.id.header_title5);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemText1;
        TextView itemText2;
        TextView itemText3;
        TextView itemText4;
        TextView itemText5;
        String markID;
        int planID;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText1 = itemView.findViewById(R.id.item_text1);
            itemText2 = itemView.findViewById(R.id.item_text2);
            itemText3 = itemView.findViewById(R.id.item_text3);
            itemText4 = itemView.findViewById(R.id.item_text4);
            itemText5 = itemView.findViewById(R.id.item_text5);
        }
    }
}
