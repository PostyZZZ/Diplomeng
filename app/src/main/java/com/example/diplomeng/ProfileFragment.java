package com.example.diplomeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, emailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Найдем TextView для никнейма и почты
        usernameTextView = view.findViewById(R.id.textlogin);
        emailTextView = view.findViewById(R.id.textemail);

        // Получим данные пользователя из SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String username = preferences.getString("emailOrUsername", ""); // Получаем никнейм (логин)
        String email = preferences.getString("email", ""); // Получаем почту

        // Определим, по каким данным пользователь авторизовался, и заполним соответствующие поля
        if (isEmail(username)) {
            // Если пользователь авторизовался по почте, отобразим почту в поле никнейма
            usernameTextView.setText("Почта: " + username);
            emailTextView.setText("Email: " + email);
        } else {
            // Иначе отобразим логин в поле никнейма и почту в отдельном поле
            usernameTextView.setText("Логин: " + username);
            emailTextView.setText("Email: " + email);
        }

        // Настроим кнопку выхода
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    // Метод для проверки, является ли строка почтой
    private boolean isEmail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    private void logout() {
        // Удаление информации о входе пользователя из SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("rememberMe");
        editor.remove("emailOrUsername");
        editor.remove("password");
        editor.apply();

        // Перенаправление на экран входа
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Закрываем текущую активити
    }
}

