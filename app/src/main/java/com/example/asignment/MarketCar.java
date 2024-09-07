package com.example.asignment;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asignment.adapter.marketCarAdapter;
import com.example.asignment.dao.carDaos;
import com.example.asignment.models.carModels;

import java.util.ArrayList;
import java.util.List;

public class MarketCar extends AppCompatActivity {

    private RecyclerView recyclerView;
    private marketCarAdapter carAdapter;
    private List<carModels> carList = new ArrayList<>();
    private carDaos carDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_car);

        Bundle extras = getIntent().getExtras();
        Long userId = extras.getLong("userId");

        recyclerView = findViewById(R.id.rcvListCarMarket);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carDao = carDaos.getInstance(this);

        try {
            carDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
        }

        carList = carDao.getAllCars();
        carAdapter = new marketCarAdapter(carList, userId);
        recyclerView.setAdapter(carAdapter);

        Button btnTurnToListCarAdmin = findViewById(R.id.btnSellCar);
        btnTurnToListCarAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketCar.this, ListCarAdmin.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        Button btnTurnToMyCars = findViewById(R.id.btnMyCar);
        btnTurnToMyCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketCar.this, MyCars.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        carList.clear();
        carList.addAll(carDao.getAllCars());
        carAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carDao.close();
    }
}