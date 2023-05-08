package com.sneva.heywalls;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sneva.easyprefs.EasyPrefs;
import com.sneva.heywalls.databinding.ActivityFirstTimeBinding;
import com.sneva.heywalls.utlis.AdRequestBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FirstTimeActivity extends AppCompatActivity {

    ActivityFirstTimeBinding binding;
    Activity activity = FirstTimeActivity.this;
    private String selectedImage;
    private String name = "HeyWalls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AdRequestBuilder.initialize(FirstTimeActivity.this);
        AdRequestBuilder.loadBanner(FirstTimeActivity.this, binding.adView);

        if (EasyPrefs.use().isKeyExist("saved")) {
            binding.back.setOnClickListener(v -> showBackDialog());
        } else {
            binding.back.setVisibility(View.GONE);
        }

        if (EasyPrefs.use().isKeyExist("saved")) {
            if (!EasyPrefs.use().getString("profile", "").equals("no")) {
                if( !EasyPrefs.use().getString("profile", "").equalsIgnoreCase("") ){
                    byte[] b = Base64.decode(EasyPrefs.use().getString("profile", ""), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    binding.profile.setImageBitmap(bitmap);
                }
            }
            binding.name.setText(EasyPrefs.use().getString("name", ""));
        }

        binding.profile.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setTitle("Choose Options");
            builder.setMessage("choose a method to select profile photo!, note we never save your data to server.");

            builder.setPositiveButton("Gallery", (dialog, which) -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 12);
                dialog.dismiss();
            });

            builder.setNeutralButton("Camera", (dialog, which) -> {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 16);
                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });

        binding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.save.setOnClickListener(v -> {
            if (selectedImage != null) {
                if (!EasyPrefs.use().isKeyExist("saved")) {
                    EasyPrefs.use().setBoolean("saved", true);
                }

                EasyPrefs.use().setString("profile", selectedImage);
                EasyPrefs.use().setString("name", name);
                startActivity(new Intent(activity, MainActivity.class));
                finish();
            } else {
                if (!EasyPrefs.use().isKeyExist("saved")) {
                    EasyPrefs.use().setBoolean("saved", true);
                }

                EasyPrefs.use().setString("profile", "no");
                EasyPrefs.use().setString("name", name);
                startActivity(new Intent(activity, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap photo = (Bitmap) BitmapFactory.decodeStream(inputStream);
                    binding.profile.setImageBitmap(photo);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    selectedImage = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle("Close Edit Profile");
        builder.setMessage("Do you want to close edit profile screen?");

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Yes, Close", (dialog, which) -> {
            dialog.dismiss();
            startActivity(new Intent(activity, MainActivity.class));
            finish();
        });

        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }
}