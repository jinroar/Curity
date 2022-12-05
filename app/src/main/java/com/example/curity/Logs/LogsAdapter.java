package com.example.curity.Logs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curity.Objects.UserLogs;
import com.example.curity.R;

import java.util.ArrayList;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.MyViewHolder> {
    private ArrayList<UserLogs> userLogsList;

    public LogsAdapter(ArrayList<UserLogs> userLogsList){
        this.userLogsList = userLogsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewEmail, textViewNumber,date;

        public MyViewHolder(final View view){
            super(view);

            textViewName = view.findViewById(R.id.name);
            textViewEmail = view.findViewById(R.id.email);
            textViewNumber = view.findViewById(R.id.number);
        }
    }


    @NonNull
    @Override
    public LogsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.logs,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsAdapter.MyViewHolder holder, int position) {
        String name = userLogsList.get(position).getUserName();
        String email = userLogsList.get(position).getEmail();
        String number = userLogsList.get(position).getPhoneNumber();
        holder.textViewName.setText(name);
        holder.textViewEmail.setText(email);
        holder.textViewNumber.setText(number);
    }

    @Override
    public int getItemCount() {
        return userLogsList.size();
    }
}
