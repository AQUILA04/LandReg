package com.lesadrax.registrationclient.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;

import java.util.List;

public class ActorPickerAdapter extends RecyclerView.Adapter<ActorPickerAdapter.MyViewHolder> {


    private Context context;
    private List<Actor> data;

    private OnSelectItemListener onSelectItemListener;

    public ActorPickerAdapter(Context context, List<Actor> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.item_actor2, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        final Actor d = data.get(i);

        holder.header.setText(d.getName());
        holder.sub.setText(d.getTag());

        holder.container.setOnClickListener(v -> {
            if (onSelectItemListener != null)
                onSelectItemListener.onSelect(d);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private View container;
        private TextView header, sub;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            header = itemView.findViewById(R.id.header);
            sub = itemView.findViewById(R.id.sub);
        }
    }

    public void setOnSelectItemListener(OnSelectItemListener onSelectItemListener) {
        this.onSelectItemListener = onSelectItemListener;
    }

    public interface OnSelectItemListener{
        void onSelect(Actor item);
    }

}
