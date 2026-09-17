package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingFragmentAdapter onboardingAdapter;
    private LinearLayout layoutDots;
    private MaterialButton btnNext;
    private ViewPager2 viewPager;
    private int savedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        
        androidx.activity.SystemBarStyle statusBar = androidx.activity.SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
        );
        androidx.activity.SystemBarStyle navBar = androidx.activity.SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
        );

        EdgeToEdge.enable(this, statusBar, navBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        layoutDots = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btnNext);
        TextView btnSkip = findViewById(R.id.btnSkip);

        if (savedInstanceState != null) {
            savedPosition = savedInstanceState.getInt("saved_position", 0);
        }

        onboardingAdapter = new OnboardingFragmentAdapter(this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(onboardingAdapter);
        
        setupDots();
        setCurrentDot(savedPosition);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentDot(position);
                if (position == onboardingAdapter.getItemCount() - 1) {
                    btnNext.setText(getString(R.string.onboarding_start));
                } else {
                    btnNext.setText(getString(R.string.onboarding_next));
                }
            }
        });

        // Delay setting the item slightly to allow the adapter to initialize if restoring state
        viewPager.post(() -> viewPager.setCurrentItem(savedPosition, false));

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                completeOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> completeOnboarding());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewPager != null) {
            outState.putInt("saved_position", viewPager.getCurrentItem());
        }
    }

    // setupOnboardingItems removed

    private void setupDots() {
        ImageView[] dots = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_dot_inactive));
            dots[i].setLayoutParams(params);
            layoutDots.addView(dots[i]);
        }
    }

    private void setCurrentDot(int index) {
        int childCount = layoutDots.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutDots.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_dot_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_dot_inactive));
            }
        }
    }

    private void completeOnboarding() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstRun", false).apply();
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
