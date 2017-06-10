package iak4.com.restmap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import iak4.com.restmap.Kelas.Peta;

public class Adapter extends RecyclerView.Adapter<Adapter.Wadah> {
    ArrayList<Peta> all_data;
    Context ctx;

    public Adapter(ArrayList<Peta> all_data, Context ctx) {
        this.all_data = all_data;
        this.ctx = ctx;
    }

    @Override
    public Adapter.Wadah onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter, parent, false);
        return new Wadah(v);
    }

    @Override
    public void onBindViewHolder(Adapter.Wadah holder, int position) {
        final Peta objek_peta = all_data.get(position);
        holder.text.setText(objek_peta.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bungkusan = new Bundle();
                bungkusan.putString("title", objek_peta.getTitle());
                bungkusan.putDouble("lat", objek_peta.getLat());
                bungkusan.putDouble("lng", objek_peta.getLng());

                Intent pindah = new Intent(ctx, Detail.class);
                pindah.putExtras(bungkusan);
                pindah.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(pindah);
            }
        });
    }

    @Override
    public int getItemCount() {
        return all_data.size();
    }

    public class Wadah extends RecyclerView.ViewHolder {
        TextView text;
        public Wadah(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.txt);
        }
    }
}
