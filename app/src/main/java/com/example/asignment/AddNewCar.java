package com.example.asignment;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asignment.dao.carDaos;
import com.example.asignment.models.carModels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AddNewCar extends AppCompatActivity {

    private carDaos carDao;

    private EditText etCarModels;
    private EditText etCarCompanies;
    private EditText etCarDescriptions;
    private EditText etCarPrices;
    private ImageView ivCarImage;

    private Uri imageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_car);

        Bundle extras = getIntent().getExtras();
        Long userId = extras.getLong("userId");

        carDao = carDaos.getInstance(this);

        try {
            carDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
            return;
        }

        etCarModels = findViewById(R.id.etCarModels);
        etCarCompanies = findViewById(R.id.etCarCompanies);
        etCarDescriptions = findViewById(R.id.etCarDescriptions);
        etCarPrices = findViewById(R.id.etCarPrices);
        ivCarImage = findViewById(R.id.ivCarImage);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        ivCarImage.setImageURI(imageUri);
                    }
                }
        );

        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(v -> openImageChooser());

        Button btnAddCar = findViewById(R.id.btnAddCar);
        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model = etCarModels.getText().toString();
                String company = etCarCompanies.getText().toString();
                String description = etCarDescriptions.getText().toString();
                int price = Integer.parseInt(etCarPrices.getText().toString());

                String imagePath = null;
                if (imageUri != null) {
                    imagePath = saveImageToInternalStorage(imageUri);
                }

                carModels newCar = carDao.createCar(model, company, description, price, imagePath, userId);

                if (newCar != null) {
                    Toast.makeText(AddNewCar.this, "Create new car success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddNewCar.this, "Create new car failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
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