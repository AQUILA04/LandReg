package com.lesadrax.registrationclient.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.ui.activity.ConflitActivity;
import com.lesadrax.registrationclient.ui.activity.OperationActivity;

import java.util.List;

public class ConflitAdapter extends RecyclerView.Adapter<ConflitAdapter.MyViewHolder> {


    private Context context;
    private List<Operation> data;


    public ConflitAdapter(Context context, List<Operation> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.item_task, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        final Operation d = data.get(i);

        holder.header.setText(d.getConflitTag());
        holder.b.setVisibility(View.GONE);
        holder.constatation.setText(d.getTag());
        holder.constatation.setVisibility(View.VISIBLE);
        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConflitActivity.class);
            intent.putExtra("DATA", d);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private View container;
        private TextView header;
        private TextView constatation;
        MaterialButton b;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            header = itemView.findViewById(R.id.header);
            constatation = itemView.findViewById(R.id.constatation);
            b = itemView.findViewById(R.id.pv);
        }
    }

}
