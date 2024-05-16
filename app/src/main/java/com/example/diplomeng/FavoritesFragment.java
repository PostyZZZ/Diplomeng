package com.example.diplomeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        dbHelper = new DatabaseHelper(requireContext());

        userId = getCurrentUserId();
        Log.d("FavoritesFragment", "Current userId: " + userId);
        favoritesList = dbHelper.getFavoritesByUserId(userId);

        FavoritesAdapter adapter = new FavoritesAdapter(favoritesList, dbHelper, userId);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private int getCurrentUserId() {
        SharedPreferences preferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }
}




