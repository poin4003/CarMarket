package com.example.asignment;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asignment.dao.userDaos;
import com.example.asignment.models.userModels;

public class Signup extends AppCompatActivity {

    private userDaos userDao;

    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private EditText etPhoneNumber;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userDao = userDaos.getInstance(this);

        try {
            userDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening database", Toast.LENGTH_SHORT).show();
            return;
        }

        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        etName = findViewById(R.id.etSignupName);
        etPhoneNumber = findViewById(R.id.etSignupPhoneNumber);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                }
        );

        Button btnSignup = findViewById(R.id.btnSubmitSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String name = etName.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();

                userModels newUser = userDao.signup(email, password, name, phoneNumber);

                if (newUser != null) {
                    Toast.makeText(Signup.this, "Create Account Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Signup.this, "Create Account Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnReturnLogin = findViewById(R.id.btnSignupLogin);
        btnReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                activityResultLauncher.launch(intent);
            }
        });
    }
}