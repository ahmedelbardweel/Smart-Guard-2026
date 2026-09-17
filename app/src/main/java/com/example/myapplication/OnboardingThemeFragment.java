package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class OnboardingThemeFragment extends Fragment {

    private MaterialCardView[] cards;
    private String[] themes = {
            ThemeHelper.THEME_DEFAULT, ThemeHelper.THEME_GREEN,
            ThemeHelper.THEME_BLUE, ThemeHelper.THEME_PURPLE,
            ThemeHelper.THEME_AMBER, ThemeHelper.THEME_ROSE
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_theme, container, false);

        cards = new MaterialCardView[]{
                view.findViewById(R.id.cardThemeDefault),
                view.findViewById(R.id.cardThemeGreen),
                view.findViewById(R.id.cardThemeBlue),
                view.findViewById(R.id.cardThemePurple),
                view.findViewById(R.id.cardThemeAmber),
                view.findViewById(R.id.cardThemeRose)
        };

        String currentTheme = ThemeHelper.getTheme(requireContext());
        updateSelection(currentTheme);

        for (int i = 0; i < cards.length; i++) {
            final int index = i;
            View.OnClickListener clickListener = v -> {
                if (!currentTheme.equals(themes[index])) {
                    ThemeHelper.setTheme(requireContext(), themes[index]);
                    requireActivity().recreate();
                }
            };
            cards[i].setOnClickListener(clickListener);
            View btnConnect = cards[i].findViewById(R.id.btnPreviewConnect);
            if (btnConnect != null) {
                btnConnect.setOnClickListener(clickListener);
            }
        }

        return view;
    }

    private void updateSelection(String currentTheme) {
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].equals(currentTheme)) {
                cards[i].setChecked(true);
                cards[i].setStrokeWidth((int) (2 * getResources().getDisplayMetrics().density));
            } else {
                cards[i].setChecked(false);
                cards[i].setStrokeWidth((int) (1 * getResources().getDisplayMetrics().density));
            }
        }
    }
}
