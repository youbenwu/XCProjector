package com.outmao.xcprojector;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import com.outmao.xcprojector.databinding.ActivityActivateBinding;

public class ActivateActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityActivateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityActivateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }


}