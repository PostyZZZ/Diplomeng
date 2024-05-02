package com.example.diplomeng;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.diplomeng.databinding.ActivityDictionaryBinding;

public class DictionaryActivity extends AppCompatActivity {

    ActivityDictionaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new DictionaryFragment());

        binding.dictionaryBottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.search) {
                replaceFragment(new DictionaryFragment());
            } else if (item.getItemId() == R.id.favorite) {
                replaceFragment(new FavoritesFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}


