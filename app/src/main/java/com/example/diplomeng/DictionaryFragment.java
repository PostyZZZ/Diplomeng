package com.example.diplomeng;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private Spinner spinnerDirection;
    private TextView textViewTranslation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        editTextWord = view.findViewById(R.id.editTextWord);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        spinnerDirection = view.findViewById(R.id.spinnerDirection);
        textViewTranslation = view.findViewById(R.id.textViewTranslation);

        buttonSearch.setOnClickListener(v -> {
            String word = editTextWord.getText().toString().trim();
            String direction = spinnerDirection.getSelectedItem().toString();
            if (!word.isEmpty()) {
                searchTranslation(word, direction);
            } else {
                Toast.makeText(getActivity(), "Введите слово", Toast.LENGTH_SHORT).show();
            }
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

        // Создаем запрос
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Обработка ответа API
                        JSONArray defArray = response.getJSONArray("def");
                        StringBuilder translations = new StringBuilder();
                        if (defArray.length() > 0) {
                            // Проходим по всем вариантам перевода
                            for (int i = 0; i < defArray.length(); i++) {
                                JSONObject defObject = defArray.getJSONObject(i);
                                JSONArray trArray = defObject.getJSONArray("tr");
                                if (trArray.length() > 0) {
                                    // Получаем только первый перевод
                                    JSONObject trObject = trArray.getJSONObject(0);
                                    String translation = trObject.getString("text");
                                    translations.append(translation).append("\n");
                                }
                            }
                            // Отображаем перевод
                            textViewTranslation.setText(translations.toString());
                        } else {
                            Toast.makeText(getActivity(), "Перевод не найден", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Обработка ошибок запроса
                    Toast.makeText(getActivity(), "Ошибка запроса: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        // Добавляем запрос в очередь запросов
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }
}


