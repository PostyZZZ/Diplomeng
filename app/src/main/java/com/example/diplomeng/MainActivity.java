package com.example.diplomeng;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private EditText emailOrUsernameEditText, passwordEditText;
    private Button loginButton, registerButton;
    private CheckBox rememberMeCheckbox; // Добавим CheckBox
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private int loginAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailOrUsernameEditText = findViewById(R.id.editTextlgn);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.authbtn);
        registerButton = findViewById(R.id.buttonreg);
        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String savedEmailOrUsername = sharedPreferences.getString("emailOrUsername", "");
            String savedPassword = sharedPreferences.getString("password", "");
            emailOrUsernameEditText.setText(savedEmailOrUsername);
            passwordEditText.setText(savedPassword);
            login(savedEmailOrUsername, savedPassword);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailOrUsername = emailOrUsernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (emailOrUsername.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите почту/логин и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    login(emailOrUsername, password);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
    }

    private void login(String emailOrUsername, String password) {
        boolean success = databaseHelper.checkUser(emailOrUsername, password);
        if (success) {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
            if (rememberMeCheckbox.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("rememberMe", true);
                editor.putString("emailOrUsername", emailOrUsername);
                editor.putString("password", password);
                editor.apply();
            }
        } else {
            loginAttempts++;
            if (loginAttempts >= 3) {
                // Здесь вызываем функцию, чтобы показать капчу
                showCaptcha();
            } else {
                Toast.makeText(MainActivity.this, "Неверные почта/логин или пароль", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showCaptcha() {
        SafetyNet.getClient(this).verifyWithRecaptcha("6LdG3dkpAAAAABf_oiYpLS_WPAtGimDW_MvcMJa4")
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            // Капча успешно пройдена, продолжайте вход
                            Toast.makeText(MainActivity.this, "Капча пройдена, продолжайте вход", Toast.LENGTH_SHORT).show();
                            // Здесь можно продолжить логику входа
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Ошибка капчи", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_register);

        final EditText loginEditText = dialog.findViewById(R.id.editTextloginreg);
        final EditText emailEditText = dialog.findViewById(R.id.editTextTextEmailreg);
        final EditText passwordEditText = dialog.findViewById(R.id.editTextPasswordreg);
        Button registerButton = dialog.findViewById(R.id.buttonreg);
        Button backButton = dialog.findViewById(R.id.buttonback);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = loginEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    boolean success = databaseHelper.addUser(username, email, password);
                    if (success) {
                        Toast.makeText(MainActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Закрыть диалоговое окно после регистрации
                    } else {
                        Toast.makeText(MainActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Закрыть диалоговое окно при нажатии кнопки "Назад"
            }
        });

        dialog.show();
    }
}




