package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.databinding.ActivityChangePasswordBinding;
import com.lksnext.parkingplantilla.databinding.ActivityLoginBinding;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz login (layout)
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.errorEmail.setText("");
        binding.confirmM.setText("");

        binding.changePassword.setOnClickListener(v ->{
            String email = binding.emailText.getText().toString();
            if(email.equals("")){
                binding.errorEmail.setText("Debes introducir un email.");
            }else{
                if (email.charAt(email.length() - 1) == ' ') {
                    email = email.substring(0, email.length() - 1);
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task->{
                            if(task.isSuccessful()){
                                binding.errorEmail.setText("");
                                binding.confirmM.setText("Email envidado.");
                                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                binding.errorEmail.setText("Ha habido un error, espera un poco o vuelvelo a intentar con otro email.");
                                binding.confirmM.setText("");
                            }
                        });
            }
        });
    }
}
