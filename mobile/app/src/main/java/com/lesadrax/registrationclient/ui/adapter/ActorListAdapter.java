package com.lesadrax.registrationclient.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.ActorListModel;
import com.lesadrax.registrationclient.data.model.Role;
import com.lesadrax.registrationclient.ui.activity.AddActorActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActorListAdapter extends RecyclerView.Adapter<ActorListAdapter.ActorListViewHolder>{
    private  List<ActorListModel> actorList = new ArrayList<>();
    private  String type;
    private Context context;

    public void addActors(List<ActorListModel> actors) {
        actorList.addAll(actors);
        notifyDataSetChanged();
    }

    public void addActors(List<ActorListModel> actors, String type) {
        actorList.addAll(actors);
        this.type = type;
        notifyDataSetChanged();
    }

    public ActorListAdapter(List<ActorListModel> actors, String type, Context context) {
        this.type = type;
        this.context = context;
        this.actorList = actors;
    }

    public ActorListAdapter(Context context) {
        this.context = context;
    }

    public ActorListAdapter() {
    }

    public ActorListAdapter(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public ActorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actor, parent, false);
        return new ActorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorListViewHolder holder, int position) {
        ActorListModel actor = actorList.get(position);
        if(type != null){
            if(!actor.getName().isEmpty())
               holder.title.setText(actor.getName());
            holder.status.setText(Role.getFrenchTranslation(actor.getRegistrationStatus()));
            holder.status.setVisibility(View.VISIBLE);
        }
        else{
            Log.d("******Here", "=====> ");
            holder.title.setText(actor.getName());
            holder.status.setVisibility(View.GONE);
            holder.uin.setText(actor.getUin());

        }

        holder.subtitle.setText(Role.getRoleNameByCode(actor.getRole()));

        holder.container.setOnClickListener(v -> {
            if(!Objects.equals(type, "PENDING")){
                Intent intent = new Intent(context, AddActorActivity.class);
                intent.putExtra("ID", actor.getId());
                context.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }

    public static class ActorListViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, uin, name;
        Button status;
        View container;

        public ActorListViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            title = itemView.findViewById(R.id.header);
            subtitle = itemView.findViewById(R.id.name);
            uin = itemView.findViewById(R.id.uin);
            status = itemView.findViewById(R.id.status);
        }
    }
}
