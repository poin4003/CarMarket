package com.example.asignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MyCarsDetail extends AppCompatActivity {

    private TextView tvCarModels;
    private TextView tvCarCompanies;
    private TextView tvCarDescriptions;
    private TextView tvCarPrices;
    private ImageView ivCarimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars_detail);

        Bundle extras = getIntent().getExtras();
        String model = extras.getString("model");
        String company = extras.getString("company");
        String description = extras.getString("description");
        String price = extras.getString("price");
        String imagePath = extras.getString("imagePath");

        tvCarModels = findViewById(R.id.tvMyCarsModel);
        tvCarCompanies = findViewById(R.id.tvMyCarsCompany);
        tvCarDescriptions = findViewById(R.id.tvMyCarsDescription);
        tvCarPrices = findViewById(R.id.tvMyCarsPrice);
        ivCarimage = findViewById(R.id.ivMyCarsImage);

        tvCarModels.setText("Model: " + model);
        tvCarCompanies.setText("Company: " + company);
        tvCarDescriptions.setText("Description: " + "\n" + description);
        tvCarPrices.setText("Price: " + price);

        Glide.with(this).load(imagePath).into(ivCarimage);

        Button btnReturn = findViewById(R.id.btnMyCarsReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }
}