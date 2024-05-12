package com.example.diplomeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DictionaryFragment extends Fragment {

    private EditText editTextWord;
    private Button buttonSearch;
    private Button buttonAddToFavorites;
    private Spinner spinnerDirection;
    private TextView textViewTranslation;

    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        editTextWord = view.findViewById(R.id.editTextWord);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonAddToFavorites = view.findViewById(R.id.buttonFavorite);
        spinnerDirection = view.findViewById(R.id.spinnerDirection);
        textViewTranslation = view.findViewById(R.id.textViewTranslation);

        dbHelper = new DatabaseHelper(requireContext());

        buttonSearch.setOnClickListener(v -> {
            String word = editTextWord.getText().toString().trim();
            String direction = spinnerDirection.getSelectedItem().toString();
            if (!word.isEmpty()) {
                searchTranslation(word, direction);
            } else {
                Toast.makeText(getActivity(), "Введите слово", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAddToFavorites.setOnClickListener(v -> {
            addToFavorites();
        });

        return view;
    }

    private void searchTranslation(String word, String direction) {
        String apiKey = "dict.1.1.20240502T143348Z.819c21474e82c17f.e87bfe67f4870df0ba6cb0471e2abba52486a7be";
        String lang;
        if (direction.equals("С русского на английский")) {
            lang = "ru-en";
        } else {
            lang = "en-ru";
        }
        String url = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup" +
                "?key=" + apiKey +
                "&lang=" + lang +
                "&text=" + word;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray defArray = response.getJSONArray("def");
                        StringBuilder translations = new StringBuilder();
                        if (defArray.length() > 0) {
                            for (int i = 0; i < defArray.length(); i++) {
                                JSONObject defObject = defArray.getJSONObject(i);
                                JSONArray trArray = defObject.getJSONArray("tr");
                                if (trArray.length() > 0) {
                                    JSONObject trObject = trArray.getJSONObject(0);
                                    String translation = trObject.getString("text");
                                    translations.append(translation).append("\n");
                                }
                            }
                            textViewTranslation.setText(translations.toString());

                            saveToFavorites(word, translations.toString());
                        } else {
                            Toast.makeText(getActivity(), "Перевод не найден", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Ошибка запроса: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }

    private void addToFavorites() {
    }

    private void saveToFavorites(String word, String translation) {
        // Получаем ID текущего пользователя
        int userId = getCurrentUserId();

        boolean saved = dbHelper.addFavorite(userId, word, translation);
        if (saved) {
            Toast.makeText(requireContext(), "Слово добавлено в избранное", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Не удалось добавить слово в избранное", Toast.LENGTH_SHORT).show();
        }
    }

    private int getCurrentUserId() {

        SharedPreferences preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }
}


