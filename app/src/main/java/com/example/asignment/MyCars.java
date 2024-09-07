package com.example.asignment;

import android.database.SQLException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asignment.adapter.myCarAdapter;
import com.example.asignment.dao.carDaos;
import com.example.asignment.models.carModels;

import java.util.ArrayList;
import java.util.List;

public class MyCars extends AppCompatActivity {

    private RecyclerView recyclerView;
    private myCarAdapter carAdapter;
    private List<carModels> carList = new ArrayList<>();
    private carDaos carDao;
    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);

        Bundle extras = getIntent().getExtras();
        userId = extras.getLong("userId");

        recyclerView = findViewById(R.id.rcvListMyCars);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carDao = carDaos.getInstance(this);

        try {
            carDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
        }

        carList = carDao.getCarsByBillUserId(userId);
        carAdapter = new myCarAdapter(carList);
        recyclerView.setAdapter(carAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carList.clear();
        carList.addAll(carDao.getCarsByBillUserId(userId));
        carAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carDao.close();
    }
}