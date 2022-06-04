package com.woofcare.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.woofcare.Dialogs.ViewLocalImageDialog;
import com.woofcare.R;

import java.util.ArrayList;

public class LocalPhotoAdapter extends RecyclerView.Adapter<LocalPhotoAdapter.localPhotoViewHolder>{

    Context context;
    ArrayList<Uri> arrUri = new ArrayList<>();

    public LocalPhotoAdapter(Context context, ArrayList<Uri> arrUri) {
        this.context = context;
        this.arrUri = arrUri;
    }

    @NonNull
    @Override
    public LocalPhotoAdapter.localPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_photo, parent, false);
        return new LocalPhotoAdapter.localPhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalPhotoAdapter.localPhotoViewHolder holder, int position) {
        Picasso.get().load(arrUri.get(position)).fit().centerCrop().into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return arrUri.size();
    }

    public class localPhotoViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView ivPhoto;

        public localPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putString("image_local_string_uri", arrUri.get(getAdapterPosition()).toString());
                    ViewLocalImageDialog viewLocalImage = new ViewLocalImageDialog();
                    viewLocalImage.setArguments(args);
                    viewLocalImage.show(((FragmentActivity)context).getSupportFragmentManager(), "TAG");
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    arrUri.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    Toast.makeText(context, "Removed selected photo", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }
}