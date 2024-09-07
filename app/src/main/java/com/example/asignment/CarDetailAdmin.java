package com.example.asignment;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asignment.dao.carDaos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CarDetailAdmin extends AppCompatActivity {

    private carDaos carDao;

    private TextView etModelAdmin;
    private TextView etCompanyAdmin;
    private TextView etDescriptionAdmin;
    private TextView etPriceAdmin;
    private ImageView ivCarAdmin;

    private Uri imageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail_admin);

        carDao = carDaos.getInstance(this);

        try {
            carDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
        }

        etModelAdmin = findViewById(R.id.etCarModelAdmin);
        etCompanyAdmin = findViewById(R.id.etCarCompanieAdmin);
        etDescriptionAdmin = findViewById(R.id.etCarDescriptionAdmin);
        etPriceAdmin = findViewById(R.id.etCarPriceAdmin);
        ivCarAdmin = findViewById(R.id.ivCarImageAdmin);

        Bundle extras = getIntent().getExtras();
        String model = extras.getString("model");
        String company = extras.getString("company");
        String description = extras.getString("description");
        String price = extras.getString("price");
        String imagePath = extras.getString("imagePath");
        Long carId = extras.getLong("carId");

        etModelAdmin.setText(model);
        etCompanyAdmin.setText(company);
        etDescriptionAdmin.setText(description);
        etPriceAdmin.setText(price);

        Glide.with(this).load(imagePath).into(ivCarAdmin);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        ivCarAdmin.setImageURI(imageUri);
                    }
                }
        );

        Button btnSelectImage = findViewById(R.id.btnSelectImageAdmin);
        btnSelectImage.setOnClickListener(v -> openImageChooser());

        Button btnUpdateCar = findViewById(R.id.btnUpdateCar);
        btnUpdateCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modelForUpdate = etModelAdmin.getText().toString();
                String companyForUpdate = etCompanyAdmin.getText().toString();
                String descriptionForUpdate = etDescriptionAdmin.getText().toString();
                int priceForUpdate = Integer.parseInt(etPriceAdmin.getText().toString());

                String imagePathForUpdate = null;
                if (imageUri != null) {
                    imagePathForUpdate = saveImageToInternalStorage(imageUri);
                }

                boolean checkUpdate = carDao.updateCar(
                        carId,
                        modelForUpdate,
                        companyForUpdate,
                        descriptionForUpdate,
                        priceForUpdate,
                        imagePathForUpdate);

                if (checkUpdate) {
                    Toast.makeText(CarDetailAdmin.this, "Update car successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CarDetailAdmin.this, "Update car failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnDeleteCar = findViewById(R.id.btnDeleteCar);
        btnDeleteCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkDelete = carDao.deleteCar(carId);

                if (checkDelete) {
                    finish();
                } else {
                    Toast.makeText(CarDetailAdmin.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCancel = findViewById(R.id.btnCancelAdmin);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            File directory = getFilesDir();
            File file = new File(directory, "car_image_" + System.currentTimeMillis() + ".png");
            try (OutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}