package com.example.diplomeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<String> favoritesList;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        dbHelper = new DatabaseHelper(requireContext());

        // Получаем избранные слова из базы данных
        int userId = getCurrentUserId();
        favoritesList = dbHelper.getFavoritesByUserId(userId);

        // Создаем и устанавливаем адаптер
        FavoritesAdapter adapter = new FavoritesAdapter(favoritesList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Метод для получения ID текущего пользователя
    private int getCurrentUserId() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }
}


