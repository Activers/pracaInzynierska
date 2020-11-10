package com.example.pracainzynierska;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerView";

    private Context mContext;
    private ArrayList<Model> modelList;

    public RecyclerAdapter(Context mContext, ArrayList<Model> modelList) {
        this.mContext = mContext;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewGame.setText(modelList.get(position).getmGame());
        holder.textViewUser.setText(modelList.get(position).getmUser());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewGame;
        TextView textViewUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewGame = itemView.findViewById(R.id.list_game);
            textViewUser = itemView.findViewById(R.id.list_username);

        }
    }

}