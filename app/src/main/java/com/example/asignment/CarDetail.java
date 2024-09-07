package com.example.asignment;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asignment.dao.billDaos;
import com.example.asignment.dao.carDaos;
import com.example.asignment.models.billModels;
import com.example.asignment.models.carModels;

public class CarDetail extends AppCompatActivity {

    private billDaos billDao;
    private carDaos carDao;

    private TextView tvModel;
    private TextView tvCompany;
    private TextView tvDescription;
    private TextView tvPrice;
    private ImageView ivCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        billDao = billDaos.getInstance(this);
        carDao = carDaos.getInstance(this);

        try {
            billDao.open();
            carDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
        }

        tvModel = findViewById(R.id.tvModel);
        tvCompany = findViewById(R.id.tvCompany);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        ivCar = findViewById(R.id.ivCarImage);

        Bundle extras = getIntent().getExtras();
        String model = extras.getString("model");
        String company = extras.getString("company");
        String description = extras.getString("description");
        String price = extras.getString("price");
        String imagePath = extras.getString("imagePath");
        Long carId = extras.getLong("carId");
        Long userId = extras.getLong("userId");

        tvModel.setText("Model: " + model);
        tvCompany.setText("Company: " + company);
        tvDescription.setText("Description: " + "\n" + description);
        tvPrice.setText("Price: " + price);

        Glide.with(this).load(imagePath).into(ivCar);


        Button btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnBuyCar = findViewById(R.id.btnBuyCar);
        btnBuyCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carModels car = carDao.findCarById(carId);

                if (car.getStatus() == 0) {

                    billModels newBill = billDao.createBill(userId, carId, Integer.parseInt(price));

                    if (newBill != null) {
                        Toast.makeText(CarDetail.this, "Buy success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CarDetail.this, "Buy Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CarDetail.this, "This car has been sold!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }
}