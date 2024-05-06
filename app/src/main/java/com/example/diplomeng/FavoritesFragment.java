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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private Set<String> favoritesSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        favoritesSet = sharedPreferences.getStringSet("words", new HashSet<>());

        List<String> favoritesList = new ArrayList<>(favoritesSet);
        FavoritesAdapter adapter = new FavoritesAdapter(favoritesList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}

