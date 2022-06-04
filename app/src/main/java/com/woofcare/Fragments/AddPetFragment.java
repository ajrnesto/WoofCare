package com.woofcare.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.woofcare.Objects.Pet;
import com.woofcare.R;
import com.woofcare.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class AddPetFragment extends Fragment {

    private static final FirebaseDatabase WOOF_CARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseStorage WOOF_CARE_ST = FirebaseStorage.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbPets;
    private StorageReference stPets = WOOF_CARE_ST.getReference("petPhotos");

    View view;
    TextInputEditText etName, etBirthday;
    AutoCompleteTextView menuSex;
    MaterialButton btnSave;
    RoundedImageView imgPet;
    MaterialCardView cvLoading;

    String[] sexes;
    ArrayAdapter<String> adapterSex;

    MaterialDatePicker.Builder<Long> builderDatePicker;
    MaterialDatePicker<Long> datePicker;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;

    private Uri uriLocalImage;
    private Context context;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        initialize();
        initializeSpinner();
        initializeDatePicker();

        etBirthday.setOnClickListener(view -> {
            etBirthday.setEnabled(false);
            datePicker.show(getParentFragmentManager(), "Date Picker");
        });

        btnSave.setOnClickListener(view -> {
            try {
                savePet();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        imgPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        return view;
    }

    private void initialize() {
        etName = view.findViewById(R.id.etName);
        etBirthday = view.findViewById(R.id.etBirthday);
        menuSex = view.findViewById(R.id.menuSex);
        btnSave = view.findViewById(R.id.btnSave);
        imgPet = view.findViewById(R.id.imgPet);
        cvLoading = view.findViewById(R.id.cvLoading);

        context = getContext();
    }

    private void initializeSpinner() {
        sexes = new String[] {"Male", "Female"};
        adapterSex = new ArrayAdapter<>(getContext(), R.layout.list_item, sexes);
        menuSex = view.findViewById(R.id.menuSex);
        menuSex.setAdapter(adapterSex);
    }

    private void initializeDatePicker() {
        builderDatePicker = MaterialDatePicker.Builder.datePicker();
        builderDatePicker.setTitleText("Birthdate")
                .setSelection(System.currentTimeMillis());
        //datePicker = builderDatePicker.setTheme(R.style.ThemeOverlay_App_MaterialCalendar).build();
        datePicker = builderDatePicker.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            etBirthday.setText(sdf.format(datePicker.getSelection()));
            etBirthday.setEnabled(true);
        });
        datePicker.addOnNegativeButtonClickListener(view -> {
            etBirthday.setEnabled(true);
        });
        datePicker.addOnCancelListener(dialogInterface -> {
            etBirthday.setEnabled(true);
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
    }

    private void openGallery() {
        Intent iGallery = new Intent();
        iGallery.setType("image/*");
        iGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(iGallery, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            uriLocalImage = data.getData();
            Picasso.get().load(uriLocalImage).fit().centerCrop().into(imgPet);
        }
    }

    private void savePet() throws IOException {
        // hide keyboard
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // hide keyboard
        cvLoading.setVisibility(View.VISIBLE);
        if (uriLocalImage != null) {
            if (TextUtils.isEmpty(Objects.requireNonNull(etName.getText()).toString().trim()) ||
                    (datePicker.getSelection() == null) ||
                    TextUtils.isEmpty(menuSex.getText().toString().trim()) ) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                cvLoading.setVisibility(View.GONE);
                return;
            }
            String fileName = System.currentTimeMillis() + "." + Utils.getFileExtension(context, uriLocalImage);
            StorageReference refPetImage = stPets.child(fileName);

            // image compressor
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriLocalImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos); // 15% quality
            byte[] data = baos.toByteArray();
            // image compressor

            refPetImage.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        refPetImage.getDownloadUrl().addOnCompleteListener(task -> {
                            String urlPetImage = task.getResult().toString();

                            dbPets = WOOF_CARE_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid()+"_pets");
                            DatabaseReference dbNewPet = dbPets.push();
                            String petUid = dbNewPet.getKey();
                            Pet newPet = new Pet( petUid, // uid
                                    etName.getText().toString().trim(), // name
                                    urlPetImage, // photoUrl
                                    fileName, // file name
                                    menuSex.getText().toString().trim(), // sex
                                    datePicker.getSelection()); // fileName

                            dbNewPet.setValue(newPet);

                            Toast.makeText(context, "New pet has been added", Toast.LENGTH_SHORT).show();
                            cvLoading.setVisibility(View.GONE);
                            getActivity().onBackPressed();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: "+ e, Toast.LENGTH_SHORT).show();
                        cvLoading.setVisibility(View.GONE);
                    });
        }
        else {
            Toast.makeText(context, "No pet photo was selected", Toast.LENGTH_SHORT).show();
            cvLoading.setVisibility(View.GONE);
        }
    }
}