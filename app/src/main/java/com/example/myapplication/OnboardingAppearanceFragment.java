package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.mikepenz.iconics.IconicsDrawable;

public class OnboardingAppearanceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_appearance, container, false);

        MaterialCardView cardLight = view.findViewById(R.id.cardAppLight);
        MaterialCardView cardDark = view.findViewById(R.id.cardAppDark);
        MaterialCardView cardSystem = view.findViewById(R.id.cardAppSystem);

        ImageView imgLight = view.findViewById(R.id.imgAppLight);
        ImageView imgDark = view.findViewById(R.id.imgAppDark);
        ImageView imgSystem = view.findViewById(R.id.imgAppSystem);

        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
        int iconColor = typedValue.data;

        if (imgLight != null) {
            IconicsDrawable d = new IconicsDrawable(requireContext(), "gmd-wb-sunny");
            androidx.core.graphics.drawable.DrawableCompat.setTint(d, iconColor);
            imgLight.setImageDrawable(d);
        }
        if (imgDark != null) {
            IconicsDrawable d = new IconicsDrawable(requireContext(), "gmd-brightness-2");
            androidx.core.graphics.drawable.DrawableCompat.setTint(d, iconColor);
            imgDark.setImageDrawable(d);
        }
        if (imgSystem != null) {
            IconicsDrawable d = new IconicsDrawable(requireContext(), "gmd-settings");
            androidx.core.graphics.drawable.DrawableCompat.setTint(d, iconColor);
            imgSystem.setImageDrawable(d);
        }

        int currentNightMode = ThemeHelper.getNightMode(requireContext());
        updateSelection(cardLight, cardDark, cardSystem, currentNightMode);

        cardLight.setOnClickListener(v -> {
            updateSelection(cardLight, cardDark, cardSystem, ThemeHelper.MODE_LIGHT);
            ThemeHelper.setNightMode(requireContext(), ThemeHelper.MODE_LIGHT);
        });

        cardDark.setOnClickListener(v -> {
            updateSelection(cardLight, cardDark, cardSystem, ThemeHelper.MODE_DARK);
            ThemeHelper.setNightMode(requireContext(), ThemeHelper.MODE_DARK);
        });

        cardSystem.setOnClickListener(v -> {
            updateSelection(cardLight, cardDark, cardSystem, ThemeHelper.MODE_SYSTEM);
            ThemeHelper.setNightMode(requireContext(), ThemeHelper.MODE_SYSTEM);
        });

        return view;
    }

    private void updateSelection(MaterialCardView cardLight, MaterialCardView cardDark, MaterialCardView cardSystem, int currentMode) {
        int activeStroke = (int) (2 * getResources().getDisplayMetrics().density);
        int inactiveStroke = (int) (1 * getResources().getDisplayMetrics().density);

        cardLight.setChecked(currentMode == ThemeHelper.MODE_LIGHT);
        cardLight.setStrokeWidth(currentMode == ThemeHelper.MODE_LIGHT ? activeStroke : inactiveStroke);

        cardDark.setChecked(currentMode == ThemeHelper.MODE_DARK);
        cardDark.setStrokeWidth(currentMode == ThemeHelper.MODE_DARK ? activeStroke : inactiveStroke);

        cardSystem.setChecked(currentMode == ThemeHelper.MODE_SYSTEM);
        cardSystem.setStrokeWidth(currentMode == ThemeHelper.MODE_SYSTEM ? activeStroke : inactiveStroke);
    }
}
