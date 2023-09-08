package com.outmao.xcprojector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.outmao.xcprojector.databinding.ActivityActivateFailBinding;

public class ActivateFailActivity extends AppCompatActivity {

    private ActivityActivateFailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivateFailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivateFailActivity.this, ActivateActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}