package com.example.diplomeng;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<String> favoritesList;
    private DatabaseHelper dbHelper;
    private int userId;

    public FavoritesAdapter(List<String> favoritesList, DatabaseHelper dbHelper, int userId) {
        this.favoritesList = favoritesList;
        this.dbHelper = dbHelper;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String favorite = favoritesList.get(position);
        holder.textViewWordTranslation.setText(favorite);

        holder.imageButtonDelete.setOnClickListener(v -> {
            String[] parts = favorite.split(" - ");
            if (parts.length > 0) {
                String word = parts[0];
                Log.d("FavoritesAdapter", "Trying to delete favorite: " + word + " for userId: " + userId);
                boolean deleted = dbHelper.deleteFavorite(userId, word);
                if (deleted) {
                    favoritesList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, favoritesList.size());
                    Toast.makeText(holder.itemView.getContext(), "Слово удалено из избранного", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Не удалось удалить слово из избранного", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWordTranslation;
        ImageButton imageButtonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWordTranslation = itemView.findViewById(R.id.textViewWordTranslation);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
        }
    }
}

