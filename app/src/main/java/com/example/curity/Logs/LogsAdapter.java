package com.example.curity.Logs;

import android.content.Context;
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
    private Context context;

    public LogsAdapter(Context context, ArrayList<UserLogs> userLogsList){
        this.context = context;
        this.userLogsList = userLogsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewEmail, textViewNumber,date;

        public MyViewHolder(final View view){
            super(view);

            textViewName = view.findViewById(R.id.name);
            textViewEmail = view.findViewById(R.id.email);
            textViewNumber = view.findViewById(R.id.number);
            date = view.findViewById(R.id.date);
        }
    }


    @NonNull
    @Override
    public LogsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_logs,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsAdapter.MyViewHolder holder, int position) {
        String name = userLogsList.get(position).getUserName();
        String email = userLogsList.get(position).getEmail();
        String number = userLogsList.get(position).getPhoneNumber();
        String date = userLogsList.get(position).getDate();
        holder.textViewName.setText(name);
        holder.textViewEmail.setText(email);
        holder.textViewNumber.setText(number);
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return userLogsList.size();
    }
}
