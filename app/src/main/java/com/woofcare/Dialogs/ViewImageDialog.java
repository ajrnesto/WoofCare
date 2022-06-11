package com.woofcare.Dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;
import com.woofcare.R;

import java.util.Objects;

public class ViewImageDialog extends DialogFragment {

    ZoomageView ivFullImage;
    String photoUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_full_image, container);
        Objects.requireNonNull(getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);

        initiate(view);

        if (requireArguments().getString("photo_url") != null) {
            photoUrl = requireArguments().getString("photo_url");
            Picasso.get().load(Uri.parse(photoUrl)).into(ivFullImage);
        }
        return view;
    }

    private void initiate(View view) {
        ivFullImage = view.findViewById(R.id.ivFullImage);
    }
}
