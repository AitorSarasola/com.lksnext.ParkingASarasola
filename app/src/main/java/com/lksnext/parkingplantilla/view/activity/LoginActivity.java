package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.ActivityLoginBinding;
import com.lksnext.parkingplantilla.viewmodel.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz login (layout)
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de login
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.errorLogin.setText("");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if(preferences.getBoolean("keepSignedIn",true))
            loginViewModel.checkLogged();
        else
            auth.signOut();

        //Acciones a realizar cuando el usuario clica el boton de login
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString();
            String password = binding.passwordText.getText().toString();
            loginViewModel.loginUser(email, password);
        });

        //Acciones a realizar cuando el usuario clica el boton de crear cuenta (se cambia de pantalla)
        binding.createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.changePassword.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        //Observamos la variable logged, la cual nos informara cuando el usuario intente hacer login y se
        //cambia de pantalla en caso de login correcto
        loginViewModel.isLogged().observe(this, logged -> {
            if (logged != null) {
                if (logged) {
                    //Login Correcto
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("keepSignedIn", binding.checkbox.isChecked());
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else{
                binding.errorLogin.setText("");
            }
        });

        loginViewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                binding.errorLogin.setText(errorMsg);
            }
        });
    }
}