package com.example.asignment;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asignment.dao.userDaos;
import com.example.asignment.models.userModels;

public class Login extends AppCompatActivity {

    private userDaos userDao;

    private EditText etEmail;
    private EditText etPassword;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDao = userDaos.getInstance(this);

        try {
            userDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                }
        );
        
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                userModels user = userDao.signin(email, password);

                if (user != null) {
                    Intent intent = new Intent(Login.this, MarketCar.class);
                    intent.putExtra("userId", user.getId());
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(Login.this, "Email or password was wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Login.this, Signup.class);
               activityResultLauncher.launch(intent);
            }
        });
    }
}