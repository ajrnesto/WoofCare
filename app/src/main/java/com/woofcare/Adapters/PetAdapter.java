package com.woofcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.woofcare.Objects.Pet;
import com.woofcare.R;

import java.util.ArrayList;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.petViewHolder>{

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbPets;

    Context context;
    ArrayList<Pet> arrPets = new ArrayList<>();
    private PetAdapter.OnPetListener mOnPetListener;

    public PetAdapter(Context context, ArrayList<Pet> arrPets, PetAdapter.OnPetListener onPetListener) {
        this.context = context;
        this.arrPets = arrPets;
        this.mOnPetListener = onPetListener;
    }

    @NonNull
    @Override
    public PetAdapter.petViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_pet, parent, false);
        return new PetAdapter.petViewHolder(view, mOnPetListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.petViewHolder holder, int position) {
        Pet pet = arrPets.get(position);

        String name = pet.getName();
        String photoUrl = pet.getPhotoUrl();

        holder.tvName.setText(name);
        Picasso.get().load(photoUrl).fit().centerCrop().into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return arrPets.size();
    }

    public class petViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        RoundedImageView ivPhoto;
        PetAdapter.OnPetListener onPetListener;

        public petViewHolder(@NonNull View itemView, PetAdapter.OnPetListener onPetListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);

            this.onPetListener = onPetListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPetListener.onPetClick(getAdapterPosition());
        }
    }

    public interface OnPetListener{
        void onPetClick(int position);
    }
}