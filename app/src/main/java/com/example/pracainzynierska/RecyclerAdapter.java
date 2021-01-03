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

    private static final String TAG = "RecyclerView";

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
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewGame;
        TextView textViewUser;
        ImageView imageViewDelete;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView, final OnItemClickedListener listener) {
            super(itemView);

            textViewGame = itemView.findViewById(R.id.textViewGame);
            textViewUser = itemView.findViewById(R.id.textViewUsername);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
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
