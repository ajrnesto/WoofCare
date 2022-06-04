package com.woofcare.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.woofcare.Dialogs.ViewImageDialog;
import com.woofcare.Objects.Photo;
import com.woofcare.R;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.localPhotoViewHolder>{

    Context context;
    ArrayList<Photo> arrPhotos = new ArrayList<>();

    public PhotoAdapter(Context context, ArrayList<Photo> arrPhotos) {
        this.context = context;
        this.arrPhotos = arrPhotos;
    }

    @NonNull
    @Override
    public PhotoAdapter.localPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_photo, parent, false);
        return new PhotoAdapter.localPhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.localPhotoViewHolder holder, int position) {
        Photo photo = arrPhotos.get(position);

        Picasso.get().load(photo.getPhotoUrl()).fit().centerCrop().into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return arrPhotos.size();
    }

    public class localPhotoViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView ivPhoto;

        public localPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);

            itemView.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putString("photo_url", arrPhotos.get(getAdapterPosition()).getPhotoUrl());
                ViewImageDialog viewImageDialog = new ViewImageDialog();
                viewImageDialog.setArguments(args);
                viewImageDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "VIEW_IMAGE");
            });
        }
    }
}