package com.example.suivichantier;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;

import java.util.List;

public class MyAdapterPV extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final List<Pv> items;
    private final String nomClient;
    private int chantierID;
    private int entrepriseID;
    private final String nomChantier;
    private final String typeEntreprise;
    private final String nomEntreprise;
    //private final AppDatabase mDatabase;

    public MyAdapterPV(Context context,List<Pv> items ,String nomClient,String nomChantier,int chantierId,String nomEntreprise,int entrepriseID,String typeEntreprise ) {
        this.context = context;
        this.items = items;
        this.entrepriseID=entrepriseID;
        this.chantierID= chantierId;
        this.typeEntreprise=typeEntreprise;
        this.nomChantier=nomChantier;
        this.nomClient=nomClient;
        this.nomEntreprise=nomEntreprise;
        //mDatabase = Room.databaseBuilder(context, AppDatabase.class, "my-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_pv, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pv, parent, false);
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
            Pv item = items.get(position - 1); // Compensate for header
            itemHolder.itemText1.setText(item.getDescription());
            itemHolder.itemText2.setText(item.getFile());
            itemHolder.itemText3.setText(item.getDate());
            itemHolder.pvID=item.getPvID();


            itemHolder.itemText1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Zoomfile.class);
                    intent.putExtra("file", item.getFile());
                    intent.putExtra("type", "pvs");
                    intent.putExtra("nomClient", nomClient);
                    intent.putExtra("nomChantier", nomChantier);
                    intent.putExtra("chantierID", chantierID);
                    intent.putExtra("entrepriseID", entrepriseID);
                    intent.putExtra("nomEntreprise", nomEntreprise);
                    intent.putExtra("typeEntreprise", typeEntreprise);
                    intent.putExtra("parentActivity", "MyAdapterPV");
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


        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle1 = itemView.findViewById(R.id.header_title1);
            headerTitle2 = itemView.findViewById(R.id.header_title2);
            headerTitle3 = itemView.findViewById(R.id.header_title3);

        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemText1;
        TextView itemText2;
        TextView itemText3;

        int pvID;


        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText1 = itemView.findViewById(R.id.item_description);
            itemText2 = itemView.findViewById(R.id.item_file);
            itemText3 = itemView.findViewById(R.id.item_date);

        }
    }



}
