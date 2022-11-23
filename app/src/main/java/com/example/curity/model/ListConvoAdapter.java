package com.example.curity.model;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curity.MainActivity.HomePage;
import com.example.curity.R;
import com.example.curity.chatFragment;
import com.example.curity.listOfConvoFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ListConvoAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<UserModel> userModelArrayList;

    public ListConvoAdapter (Context context, ArrayList<UserModel> userModelArrayList){
        this.context = context;
        this.userModelArrayList = userModelArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.convo_inflater, parent, false);
        return new ListConvoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel userModel = userModelArrayList.get(position);
        ((ListConvoHolder)holder).bind(userModel);
    }

    @Override
    public int getItemCount() {
        return this.userModelArrayList.size();
    }

    private class ListConvoHolder extends RecyclerView.ViewHolder{
        TextView tv_fullname;
        MaterialCardView card;
        public ListConvoHolder(@NonNull View itemView) {
            super(itemView);
            tv_fullname = itemView.findViewById(R.id.tv_convo_fullname);
            card = itemView.findViewById(R.id.card);
        }
        void bind(UserModel userModel){
            tv_fullname.setText(userModel.fullname+"");

            card.setOnClickListener( view1 ->{
                //DITO NYO ILALAGAY UNG PAG TAWAG SA FRAGMENT CHAT
                chatFragment chatFragment1 = new chatFragment();
                Bundle b = new Bundle();
                //EXAMPLE NEED NIYo MAKUHA YUNG ID
                b.putString("uid","this is uid data");

                ((HomePage) context).replaceFragment(chatFragment1);
                /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                        new listOfConvoFragment()).commit();*/
            });
        }
    }
}
