package com.woofcare.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Utils {
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    public static void basicDialog(Context context, String title, String button){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setTitle(title);
        materialAlertDialogBuilder.setPositiveButton(button, (dialogInterface, i) -> { });
        materialAlertDialogBuilder.show();
    }
    public static void simpleDialog(Context context, String title, String message, String button){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setTitle(title);
        materialAlertDialogBuilder.setMessage(message);
        materialAlertDialogBuilder.setPositiveButton(button, (dialogInterface, i) -> { });
        materialAlertDialogBuilder.show();
    }

    public static String getDefaultPhotoUrl() {
        return "https://firebasestorage.googleapis.com/v0/b/woofcare-6ea6c.appspot.com/o/woman%20(1).png?alt=media&token=21738871-f279-4ce5-997a-699476df51df";
    }

    public static String getFileExtension(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
