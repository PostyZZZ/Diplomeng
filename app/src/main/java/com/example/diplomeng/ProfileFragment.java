package com.example.diplomeng;

import android.Manifest;  // Добавьте этот импорт
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, emailTextView;
    private ImageView avatarImageView;
    private Button logoutButton, changeAvatarButton;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 123; // Добавьте это объявление переменной
    private String selectedImagePath = null;

    private Context context;
    private DatabaseHelper databaseHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.textlogin);
        emailTextView = view.findViewById(R.id.textemail);
        avatarImageView = view.findViewById(R.id.avatarImageView);
        logoutButton = view.findViewById(R.id.logoutButton);
        changeAvatarButton = view.findViewById(R.id.changeAvatarButton);

        SharedPreferences preferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean loginByEmail = preferences.getBoolean("loginByEmail", false);
        String emailOrUsername = preferences.getString("emailOrUsername", "");

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Если разрешение не предоставлено, запросите его у пользователя
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        } else {
            // Если разрешение уже предоставлено, загрузите изображение аватара
            loadAvatarImage();
        }

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

        int userId = preferences.getInt("userId", -1);
        if (userId != -1) {
            String avatarPath = databaseHelper.getAvatarPathByUserId(userId);
            if (avatarPath != null) {
                File imgFile = new File(avatarPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    avatarImageView.setImageBitmap(myBitmap);
                }
            }
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadAvatarImage();
            } else {
                // Разрешение не предоставлено
                Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = getRealPathFromURI(selectedImageUri);
            if (selectedImagePath != null) {
                // Обновляем путь к аватару в базе данных
                SharedPreferences preferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                int userId = preferences.getInt("userId", -1);
                if (userId != -1) {
                    boolean updated = databaseHelper.updateAvatarPath(userId, selectedImagePath);
                    if (updated) {
                        // Обновляем отображение аватара
                        File imgFile = new File(selectedImagePath);
                        if (imgFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            avatarImageView.setImageBitmap(myBitmap);
                        }
                    } else {
                        Toast.makeText(context, "Failed to update avatar path in database", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Failed to get image path", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        SharedPreferences preferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("rememberMe");
        editor.remove("loginByEmail");
        editor.remove("emailOrUsername");
        editor.remove("password");
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    private void loadAvatarImage() {
        // Получите путь к изображению аватара из базы данных
        SharedPreferences preferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        if (userId != -1) {
            String avatarPath = databaseHelper.getAvatarPathByUserId(userId);
            if (avatarPath != null) {
                // Загрузите изображение аватара из файла и установите его в ImageView
                File imgFile = new File(avatarPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    avatarImageView.setImageBitmap(myBitmap);
                }
            }
        }
    }
}
