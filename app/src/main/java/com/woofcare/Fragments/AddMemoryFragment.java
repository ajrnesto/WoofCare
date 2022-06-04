package com.woofcare.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.woofcare.Adapters.LocalPhotoAdapter;
import com.woofcare.Objects.Memory;
import com.woofcare.R;
import com.woofcare.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AddMemoryFragment extends Fragment {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseStorage WOOF_CARE_ST = FirebaseStorage.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbMemories, dbMemoryPhotos;
    private StorageReference stMemories = WOOF_CARE_ST.getReference("memoryPhotos");

    View view;
    TextInputEditText etTitle, etDate, etJournal;
    MaterialButton btnSave;
    MaterialCardView cvLoading;
    TextView tvLoading;
    LinearProgressIndicator progressBar;
    MaterialDatePicker.Builder<Long> builderDatePicker;
    MaterialDatePicker<Long> datePicker;

    // multiple image picker
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int READ_PERMISSION = 101;
    MaterialButton btnInsertPhoto;
    RecyclerView rvPhotos;
    ArrayList<Uri> arrUri = new ArrayList<>();
    LocalPhotoAdapter localPhotoAdapter;
    // multiple image picker

    private Context context;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_memory, container, false);

        initialize();
        initializeDatePicker();
        initializeImagePicker();

        etDate.setOnClickListener(view -> {
            etDate.setEnabled(false);
            datePicker.show(getParentFragmentManager(), "Date Picker");
        });

        btnSave.setOnClickListener(view -> {
            try {
                saveMemory();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    private void initialize() {
        etTitle = view.findViewById(R.id.etTitle);
        etDate = view.findViewById(R.id.etDate);
        etJournal = view.findViewById(R.id.etJournal);
        cvLoading = view.findViewById(R.id.cvLoading);
        tvLoading = view.findViewById(R.id.tvLoading);
        btnSave = view.findViewById(R.id.btnSave);
        progressBar = view.findViewById(R.id.linearProgressIndicator);

        context = getContext();
    }

    private void initializeDatePicker() {
        builderDatePicker = MaterialDatePicker.Builder.datePicker();
        builderDatePicker.setTitleText("Event date")
                .setSelection(System.currentTimeMillis());
        //datePicker = builderDatePicker.setTheme(R.style.ThemeOverlay_App_MaterialCalendar).build();
        datePicker = builderDatePicker.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            etDate.setText(sdf.format(datePicker.getSelection()));
            etDate.setEnabled(true);
        });
        datePicker.addOnNegativeButtonClickListener(view -> {
            etDate.setEnabled(true);
        });
        datePicker.addOnCancelListener(dialogInterface -> {
            etDate.setEnabled(true);
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
    }

    private void initializeImagePicker() {
        rvPhotos = view.findViewById(R.id.rvPhotos);
        btnInsertPhoto = view.findViewById(R.id.btnInsertPhoto);
        localPhotoAdapter = new LocalPhotoAdapter(context, arrUri);
        rvPhotos.setLayoutManager(new GridLayoutManager(context, 2));
        rvPhotos.setAdapter(localPhotoAdapter);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }

        btnInsertPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE_MULTIPLE) && (resultCode == Activity.RESULT_OK)) {
            if (Objects.requireNonNull(data).getClipData() != null){
                int itemCount = data.getClipData().getItemCount();

                for (int i=0; i<itemCount; i++) {
                    arrUri.add(data.getClipData().getItemAt(i).getUri());
                }
                localPhotoAdapter.notifyDataSetChanged();
            }
            else if (data.getData() != null) {
                arrUri.add(data.getData());
                localPhotoAdapter.notifyDataSetChanged();
            }
        }
    }

    private void saveMemory() throws IOException {
        // hide keyboard
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // hide keyboard

        cvLoading.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(Objects.requireNonNull(etTitle.getText()).toString().trim()) ||
                (datePicker.getSelection() == null) ||
                TextUtils.isEmpty(Objects.requireNonNull(etJournal.getText()).toString().trim()) ) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
            cvLoading.setVisibility(View.GONE);
            return;
        }
        tvLoading.setText("Uploading 0/"+arrUri.size()+" photos");
        if (!arrUri.isEmpty()) {
            // memory information
            dbMemories = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid()+"_memories");
            DatabaseReference dbNewMemory = dbMemories.push();
            String memoryUid = dbNewMemory.getKey();

            // memory photos
            for (Uri uri : arrUri) {
                tvLoading.setText("Uploading "+arrUri.size()+" photo(s)");
                String fileName = System.currentTimeMillis() + "." + Utils.getFileExtension(context, uri);
                StorageReference refMemoryImage = stMemories.child(fileName);

                // image compressor
                Bitmap bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos); // 15% quality
                byte[] data = baos.toByteArray();
                // image compressor

                refMemoryImage.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                    refMemoryImage.getDownloadUrl().addOnCompleteListener(task -> {
                        String urlMemoryImage = task.getResult().toString();
                        dbMemoryPhotos = WOOF_CARE_DB.getReference("user_"+USER.getUid()+"_memoryPhotos");
                        DatabaseReference newMemoryPhoto = dbMemoryPhotos.child(Objects.requireNonNull(memoryUid)).push();
                        newMemoryPhoto.child("uid").setValue(newMemoryPhoto.getKey());
                        newMemoryPhoto.child("photoUrl").setValue(urlMemoryImage);
                        newMemoryPhoto.child("fileName").setValue(fileName);

                        Memory newMemory = new Memory(
                                memoryUid, // uid
                                etTitle.getText().toString(), // title
                                datePicker.getSelection(), // timestamp
                                etJournal.getText().toString() // journal
                        );
                        dbNewMemory.setValue(newMemory);
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: "+ e, Toast.LENGTH_SHORT).show();
                    cvLoading.setVisibility(View.GONE);
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressBar.setIndeterminate(false);
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressBar.setProgress((int) progress);
                    }
                });
            }

            Toast.makeText(context, "New memory saved", Toast.LENGTH_SHORT).show();
            cvLoading.setVisibility(View.GONE);
            requireActivity().onBackPressed();
        }
        else {
            Toast.makeText(context, "No photos were selected", Toast.LENGTH_SHORT).show();
            cvLoading.setVisibility(View.GONE);
        }
    }
}