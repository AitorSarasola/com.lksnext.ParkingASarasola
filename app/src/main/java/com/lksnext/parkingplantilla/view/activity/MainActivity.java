package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;
import com.lksnext.parkingplantilla.domain.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    NavController navController;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz main (layout)
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Con el NavigationHost podremos movernos por distintas pestañas dentro de la misma pantalla
        NavHostFragment navHostFragment =
            (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.flFragment);
        navController = navHostFragment.getNavController();

        //Asignamos los botones de navegacion que se encuentran en la vista (layout)
        bottomNavigationView = binding.bottomNavigationView;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        NotificationHelper.createNotificationChannel(this);
        NotificationHelper.requestNotificationPermission(this);

        //Dependendiendo que boton clique el usuario de la navegacion se hacen distintas cosas
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.newres) {
                navController.navigate(R.id.mainFragment);
                return true;
            } else if (itemId == R.id.reservations) {
                navController.navigate(R.id.bookingsFragment);
                return true;
            } else if (itemId == R.id.person) {
                navController.navigate(R.id.profileFragment);
                return true;
            }
            return false;
        });

        // Lee el extra para navegar al perfil
        boolean navigateToProfile = getIntent().getBooleanExtra("navigateToProfile", false);
        if (navigateToProfile) {
            navController.navigate(R.id.profileFragment);
            bottomNavigationView.setSelectedItemId(R.id.person);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}