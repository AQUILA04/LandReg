package com.lesadrax.registrationclient.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.ui.activity.AddActorActivity;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.MyViewHolder> {

    private Context context;
    private List<Actor> data;
    private boolean selectable;

    public ActorAdapter(Context context, List<Actor> data) {
        this.context = context;
        this.data = data;
    }

    public ActorAdapter(Context context, List<Actor> data, boolean selectable) {
        this.context = context;
        this.data = data;
        this.selectable = selectable;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.item_actor, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        final Actor d = data.get(i);

        holder.header.setText(d.getRole());
        holder.name.setText(d.getName());
        holder.uin.setText(d.getTag());


        holder.container.setOnClickListener(v -> {
            Log.d("*****FV", "======> "+d.getFormValues().toString());
            Intent intent = new Intent(context, AddActorActivity.class);
            intent.putExtra("TASK", d.getId());
            intent.putExtra("ACTOR", d);
            context.startActivity(intent);
        });

        if(d.getMessage() != null && !d.getMessage().isEmpty()){
            holder.errorIcon.setVisibility(View.VISIBLE);
            holder.messageIcon.setVisibility(View.VISIBLE);

            holder.messageIcon.setOnClickListener(v->{
                SweetAlertDialog infoDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
                infoDialog.setTitleText("Information")
                        .setContentText(d.getMessage())
                        .setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            });
        }
        if (!selectable) {
            holder.container.setOnClickListener(v -> {
                Log.d("*****FV", "======> " + d.getFormValues().toString());
                Intent intent = new Intent(context, AddActorActivity.class);
                intent.putExtra("TASK", d.getId());
                intent.putExtra("ACTOR", d);
                context.startActivity(intent);
            });
        } else {

            if (d.isSelected())
                holder.check.setVisibility(View.VISIBLE);
            else
                holder.check.setVisibility(View.GONE);

            holder.container.setOnClickListener(v -> {
                d.setSelected(!d.isSelected());
//                notifyDataSetChanged();
                notifyItemChanged(i);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<Actor> getSelectedData(){
        List<Actor> list = new ArrayList<>();
        for (Actor d : data){
            if (d.isSelected()) list.add(d);
        }

        return list;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private View container, check;
        private TextView header, name, uin;
        private ImageView errorIcon, messageIcon;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            header = itemView.findViewById(R.id.header);
            name = itemView.findViewById(R.id.name);
            uin = itemView.findViewById(R.id.uin);
            check = itemView.findViewById(R.id.check);
            errorIcon = itemView.findViewById(R.id.error_icon);
            messageIcon = itemView.findViewById(R.id.message_icon);

        }
    }

}
