package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.ActivityRegisterBinding;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de registro
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de register
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        binding.errorRegister.setText("");
        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.usernameText.getText().toString();
            String email = binding.emailText.getText().toString();
            String password = binding.passwordText.getText().toString();
            String confirmPassword = binding.CpasswordText.getText().toString();
            String e = registerViewModel.registerUser(username, email, password, confirmPassword);
            binding.errorRegister.setText(e);
        });

        //Acciones a realizar cuando el usuario clica el boton de login (se cambia de pantalla)
        binding.loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //Observamos la variable logged, la cual nos informara cuando el usuario intente hacer login y se
        //cambia de pantalla en caso de login correcto
        RegisterViewModel.isSignedUp().observe(this, signedUp -> {
            if (signedUp != null) {
                if (signedUp) {
                    //Login Correcto
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    //Login incorrecto
                }
            }
        });
    }
}