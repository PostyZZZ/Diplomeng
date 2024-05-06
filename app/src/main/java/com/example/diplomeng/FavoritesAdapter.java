package com.example.diplomeng;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<String> favoritesList;

    public FavoritesAdapter(List<String> favoritesList) {
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String favoriteItem = favoritesList.get(position);
        holder.textViewWordTranslation.setText(favoriteItem);
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewWordTranslation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWordTranslation = itemView.findViewById(R.id.textViewWordTranslation);
        }
    }
}
