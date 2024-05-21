package com.example.diplomeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, emailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.textlogin);
        emailTextView = view.findViewById(R.id.textemail);

        SharedPreferences preferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean loginByEmail = preferences.getBoolean("loginByEmail", false);
        String emailOrUsername = preferences.getString("emailOrUsername", "");

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());

       if (loginByEmail) {
           String username = databaseHelper.getUsernameByEmailOrUsername(emailOrUsername);
           if (username != null) {
               usernameTextView.setText(username);
           }
           emailTextView.setText(emailOrUsername);
       } else {
           String email = databaseHelper.getEmailByUsername(emailOrUsername);
           if (email != null) {
               emailTextView.setText(email);
           }
           usernameTextView.setText(emailOrUsername);
       }

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    private void logout() {
        SharedPreferences preferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("rememberMe");
        editor.remove("loginByEmail");
        editor.remove("emailOrUsername");
        editor.remove("password");
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}

