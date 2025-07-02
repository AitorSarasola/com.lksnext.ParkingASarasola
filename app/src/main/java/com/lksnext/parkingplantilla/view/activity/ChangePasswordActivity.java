package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityChangePasswordBinding;
import com.lksnext.parkingplantilla.viewmodel.ChangePasswordViewModel;


public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private ChangePasswordViewModel changePasswordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz login (layout)
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        changePasswordViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        changePasswordViewModel.resetSent();
        binding.confirmM.setText("");

        binding.changePassword.setOnClickListener(v ->{
            String email = binding.emailText.getText().toString();
            changePasswordViewModel.changePassword(email);
        });

        binding.buttonRegresar.setOnClickListener(v->{
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        changePasswordViewModel.isSent().observe(this, sent -> {
            if (sent != null) {
                if (sent) {
                    binding.changePassword.setEnabled(false);
                    binding.changePassword.setAlpha(0.5f);
                    binding.confirmM.setText("Mensaje Enviado, Recibirás el mensaje si hay una cuenta asociada al email.");
                    binding.confirmM.setTextColor(ContextCompat.getColor(this, R.color.light_blue));
                    finish();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }, 2000);
                } else {
                    binding.confirmM.setText(changePasswordViewModel.getError().getValue());
                    binding.confirmM.setTextColor(ContextCompat.getColor(this, R.color.red));
                }
            }else{
                binding.confirmM.setText("");
            }
        });
    }
}
