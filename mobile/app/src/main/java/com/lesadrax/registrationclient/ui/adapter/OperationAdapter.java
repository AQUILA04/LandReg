package com.lesadrax.registrationclient.ui.adapter;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.ui.PVActivity;
import com.lesadrax.registrationclient.ui.activity.OperationActivity;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OperationAdapter extends RecyclerView.Adapter<OperationAdapter.MyViewHolder> {


    private Context context;
    private List<Operation> data;

    private boolean selectable;


    public OperationAdapter(Context context, List<Operation> data) {
        this.context = context;
        this.data = data;
    }


    public OperationAdapter(Context context, List<Operation> data, boolean selectable) {
        this.context = context;
        this.data = data;
        this.selectable = selectable;
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

        holder.header.setText(d.getTag());

       if(d.isCompleted())
         holder.pv.setVisibility(View.GONE);
       else{
           holder.pv.setVisibility(VISIBLE);
       }

        if(d.getMessage() != null && !d.getMessage().isEmpty()){
            holder.errorIcon.setVisibility(VISIBLE);
            holder.messageIcon.setVisibility(VISIBLE);

            holder.messageIcon.setOnClickListener(v->{
                SweetAlertDialog infoDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
                infoDialog.setTitleText("Information")
                        .setContentText(d.getMessage())
                        .setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            });
        }
        if(!selectable){
            holder.container.setOnClickListener(v -> {
                Intent intent = new Intent(context, OperationActivity.class);
                intent.putExtra("DATA", d);
                context.startActivity(intent);
            });
            holder.pv.setOnClickListener(v->{
                Intent intent = new Intent(context, PVActivity.class);
                intent.putExtra("DATA", d);
                context.startActivity(intent);
            });
        }
        else{

            if (d.isSelected())
                holder.check.setVisibility(VISIBLE);
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

    public List<Operation> getSelectedData(){
        List<Operation> list = new ArrayList<>();
        for (Operation d : data){
            if (d.isSelected()) list.add(d);
        }

        return list;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private View container, check;
        private TextView header;
        private MaterialButton pv;
        ImageView errorIcon, messageIcon;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            header = itemView.findViewById(R.id.header);
            pv = itemView.findViewById(R.id.pv);
            check = itemView.findViewById(R.id.check);
            errorIcon = itemView.findViewById(R.id.error_icon);
            messageIcon = itemView.findViewById(R.id.message_icon);
        }
    }

}
