package com.lesadrax.registrationclient.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SquareAdapter extends RecyclerView.Adapter<SquareAdapter.SquareViewHolder> {

    private final Context context;
    private final List<Integer> items;
    private final HashSet<Integer> updatedPositions = new HashSet<>(); // Pour garder en mémoire les positions mises à jour

    public SquareAdapter(Context context, List<Integer> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SquareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_check, parent, false);
        return new SquareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SquareViewHolder holder, int position) {
        holder.square.setText(String.valueOf(items.get(position)));

        // Mettre à jour la couleur de la bordure
        if (updatedPositions.contains(position)) {
            holder.square.setBackgroundResource(R.drawable.square_border_green); // Bordure verte
        } else {
            holder.square.setBackgroundResource(R.drawable.square_border_red); // Bordure verte
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updatePosition(int position) {
        if (!updatedPositions.contains(position)) {
            updatedPositions.add(position); // Ajouter la position mise à jour
            notifyItemChanged(position); // Mettre à jour l'élément dans le RecyclerView
        }
    }

    public static class SquareViewHolder extends RecyclerView.ViewHolder {
        TextView square;

        public SquareViewHolder(@NonNull View itemView) {
            super(itemView);
            square = itemView.findViewById(R.id.square);
        }
    }
}

