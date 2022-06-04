package com.woofcare.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.woofcare.R;

public class ViewImageDialog extends AppCompatDialogFragment {

    ImageView ivFullImage;
    String photoUrl;
    CircularProgressIndicator loadingBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_full_image, null);

        initiate(view);
        if (requireArguments().getString("photo_url") != null) {
            photoUrl = requireArguments().getString("photo_url");
            Picasso.get().load(Uri.parse(photoUrl)).into(ivFullImage, new Callback() {
                @Override
                public void onSuccess() {
                    loadingBar.hide();
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

        builder.setView(view);
        return builder.create();
    }

    private void initiate(View view) {
        ivFullImage = view.findViewById(R.id.ivFullImage);
        loadingBar = view.findViewById(R.id.loadingBar);
    }
}
