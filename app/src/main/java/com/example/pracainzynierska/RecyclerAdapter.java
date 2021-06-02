package com.example.pracainzynierska;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Model> modelList;
    private OnItemClickedListener mListener;

    public interface OnItemClickedListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickedListener listener) {
            mListener = listener;
        }

    public RecyclerAdapter(Context mContext, ArrayList<Model> modelList) {
            this.mContext = mContext;
            this.modelList = modelList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_my_profile,parent,false);

        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewGame.setText(modelList.get(position).getGame());
        holder.textViewUser.setText(modelList.get(position).getNick());
        holder.imageViewDelete.setVisibility(modelList.get(position).getVisibility());
        switch (modelList.get(position).getGame()) {
            case "CS:GO" :
                holder.imageViewGameLogo.setImageResource(R.drawable.ic_csgo); break;
            case "League of Legends" :
                holder.imageViewGameLogo.setImageResource(R.drawable.ic_lol); holder.imageViewGameLogo.setScaleType(ImageView.ScaleType.CENTER_CROP); break;
            case "Fortnite" :
                holder.imageViewGameLogo.setImageResource(R.drawable.ic_fortnite); break;
            case "Among Us" :
                holder.imageViewGameLogo.setImageResource(R.drawable.ic_amongus); break;
            case "PUBG" :
                holder.imageViewGameLogo.setImageResource(R.drawable.ic_pubg); break;
            case "APEX" :
                holder.imageViewGameLogo.setImageResource(R.drawable.ic_apex); break;
            default:
                holder.imageViewGameLogo.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewGame;
        TextView textViewUser;
        ImageView imageViewDelete;
        ImageView imageViewGameLogo;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView, final OnItemClickedListener listener) {
            super(itemView);

            textViewGame = itemView.findViewById(R.id.textViewGame);
            textViewUser = itemView.findViewById(R.id.textViewUsername);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            imageViewGameLogo = itemView.findViewById(R.id.imageViewGameLogo);
            relativeLayout = itemView.findViewById(R.id.list_root);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

        }
    }

}
