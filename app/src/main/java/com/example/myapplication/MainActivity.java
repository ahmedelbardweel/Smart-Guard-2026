package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            androidx.core.os.LocaleListCompat currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales();
            if (currentLocale.isEmpty()) {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                        androidx.core.os.LocaleListCompat.forLanguageTags("ar")
                );
            }
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            super.onCreate(savedInstanceState);
            return;
        }

        ThemeHelper.applyTheme(this);
        
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
        int surfaceColor = typedValue.data;
        
        // Calculate the exact Material 3 Elevation Overlay color (for 8dp elevation)
        com.google.android.material.elevation.ElevationOverlayProvider provider = 
                new com.google.android.material.elevation.ElevationOverlayProvider(this);
        float elevation = 8f * getResources().getDisplayMetrics().density;
        int bottomNavColor = provider.compositeOverlayIfNeeded(surfaceColor, elevation);

        boolean isDark = androidx.core.graphics.ColorUtils.calculateLuminance(bottomNavColor) < 0.5;

        androidx.activity.SystemBarStyle statusBar = androidx.activity.SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
        );
        androidx.activity.SystemBarStyle navBar = isDark ? 
                androidx.activity.SystemBarStyle.dark(bottomNavColor) : 
                androidx.activity.SystemBarStyle.light(bottomNavColor, bottomNavColor);

        EdgeToEdge.enable(this, statusBar, navBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }
}
