package com.example.pracainzynierska;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapterPlayers extends RecyclerView.Adapter<RecyclerAdapterPlayers.ViewHolder> {

    StorageReference fStorage;

    private Context mContext;
    private ArrayList<Model> modelListPlayers;
    private OnItemClickedListener mListener;

    public interface OnItemClickedListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickedListener listener) {
        mListener = listener;
    }

    public RecyclerAdapterPlayers(Context mContext, ArrayList<Model> modelListPlayers) {
        this.mContext = mContext;
        this.modelListPlayers = modelListPlayers;
    }

    @NonNull
    @Override
    public RecyclerAdapterPlayers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_players,parent,false);

        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.textViewPlayersNick.setText(modelListPlayers.get(position).getNick());
        holder.textViewPlayersRank.setText(modelListPlayers.get(position).getRank());

        try {
            fStorage = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = fStorage.child("users/" + modelListPlayers.get(position).getUsername() + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).resize(250, 300).noFade().rotate(270).into(holder.imageViewPlayersAvatar);
                }
            });
        } catch (Exception e) {}

        if (modelListPlayers.get(position).getMic() != null) {
            if (modelListPlayers.get(position).getMic().equals("Tak")) {
                holder.imageViewPlayersMic.setImageResource(R.drawable.ic_mic_on);
            } else {
                holder.imageViewPlayersMic.setImageResource(R.drawable.ic_mic_off);
            }
        }

    }

    @Override
    public int getItemCount() {
        return modelListPlayers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewPlayersNick;
        TextView textViewPlayersRank;
        ImageView imageViewPlayersAvatar;
        ImageView imageViewPlayersMic;
        RelativeLayout relativeLayoutPlayersCardView;

        public ViewHolder(@NonNull View itemView, final OnItemClickedListener listener) {
            super(itemView);

            textViewPlayersNick = itemView.findViewById(R.id.textViewPlayersNick);
            textViewPlayersRank = itemView.findViewById(R.id.textViewPlayersRank);
            imageViewPlayersAvatar = itemView.findViewById(R.id.imageViewPlayersAvatar);
            imageViewPlayersMic = itemView.findViewById(R.id.imageViewPlayersMic);
            relativeLayoutPlayersCardView = itemView.findViewById(R.id.relativeLayoutPlayersCardView);

            relativeLayoutPlayersCardView.setOnClickListener(new View.OnClickListener() {
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

        }
    }

}
