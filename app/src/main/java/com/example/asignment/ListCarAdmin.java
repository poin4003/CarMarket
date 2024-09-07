package com.example.asignment;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asignment.adapter.listCarAdapter;
import com.example.asignment.dao.carDaos;
import com.example.asignment.models.carModels;

import java.util.ArrayList;
import java.util.List;

public class ListCarAdmin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private listCarAdapter carAdapter;
    private List<carModels> carList = new ArrayList<>();
    private carDaos carDao;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_car_admin);

        Bundle extras = getIntent().getExtras();
        userId = extras.getLong("userId");

        recyclerView = findViewById(R.id.recylerViewListCar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carDao = carDaos.getInstance(this);

        try {
            carDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
        }

        carList = carDao.getCarsByUserId(userId);
        carAdapter = new listCarAdapter(carList);
        recyclerView.setAdapter(carAdapter);

        Button btnAddNewCar = findViewById(R.id.btnAddNewCar);
        btnAddNewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCarAdmin.this, AddNewCar.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carList.clear();
        carList.addAll(carDao.getCarsByUserId(userId));
        carAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carDao.close();
    }
}