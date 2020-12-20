package com.example.impressionselling.rmo;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.impressionselling.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class serviceAdapter extends RecyclerView.Adapter<serviceAdapter.myViewHolder> {

    private ArrayList<serviceProduct> serviceList;
    DatabaseReference databaseReference;

    public serviceAdapter(ArrayList<serviceProduct> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);
        return new myViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Service");
        holder.serviceP.setText(serviceList.get(position).getProduct());

        if(serviceList.get(position).getStatus().equals("Generated")) {
            holder.btnStatus.setVisibility(View.GONE);
            holder.serviceD.setText(serviceList.get(position).getDate()+" | "+serviceList.get(position).getId());
        }
        else if (serviceList.get(position).getStatus().equals("Issued")) {
            holder.btnStatus.setText("Collect");
            holder.serviceD.setText(serviceList.get(position).getDate()+" | "+serviceList.get(position).getId()+
                    "\nGiven On : "+serviceList.get(position).getCollectDate());
        }
        else {
            holder.btnStatus.setVisibility(View.GONE);
            holder.serviceD.setText(serviceList.get(position).getDate()+" | "+serviceList.get(position).getId()+
                    "\nGiven On : "+serviceList.get(position).getCollectDate()+
                    "\nReceived On : "+serviceList.get(position).getCompleteDate());
        }

        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => "+c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String formattedDate = df.format(c.getTime());

                databaseReference.child(serviceList.get(position).getId()).child("status").setValue("Completed");
                databaseReference.child(serviceList.get(position).getId()).child("completeDate").setValue(formattedDate);
            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder {
        TextView serviceP, serviceD;
        Button btnStatus;
        myViewHolder(@NonNull final View itemView) {
            super(itemView);
            serviceP = itemView.findViewById(R.id.serviceProduct);
            serviceD = itemView.findViewById(R.id.serviceDetail);
            btnStatus = itemView.findViewById(R.id.btnStatus);
        }
    }
}
