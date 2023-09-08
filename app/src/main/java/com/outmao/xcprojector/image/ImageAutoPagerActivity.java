package com.outmao.xcprojector.image;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.outmao.xcprojector.R;
import com.outmao.xcprojector.databinding.ActivityActivateBinding;
import com.outmao.xcprojector.databinding.ActivityImageAutoPagerBinding;

public class ImageAutoPagerActivity extends AppCompatActivity {

    private ActivityImageAutoPagerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImageAutoPagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}