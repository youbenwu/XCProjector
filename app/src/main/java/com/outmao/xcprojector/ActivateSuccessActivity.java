package com.outmao.xcprojector;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.outmao.xcprojector.databinding.ActivityActivateSuccessBinding;

public class ActivateSuccessActivity extends AppCompatActivity {

    private ActivityActivateSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivateSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(ActivateSuccessActivity.this, MainActivity.class);
                Intent intent = new Intent(ActivateSuccessActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}