package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private RadioGroup radioGroupTheme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        radioGroupTheme = view.findViewById(R.id.radioGroupTheme);
        
        String currentTheme = ThemeHelper.getTheme(requireContext());
        switch (currentTheme) {
            case ThemeHelper.THEME_GREEN:
                radioGroupTheme.check(R.id.radioThemeGreen);
                break;
            case ThemeHelper.THEME_BLUE:
                radioGroupTheme.check(R.id.radioThemeBlue);
                break;
            case ThemeHelper.THEME_PURPLE:
                radioGroupTheme.check(R.id.radioThemePurple);
                break;
            case ThemeHelper.THEME_AMBER:
                radioGroupTheme.check(R.id.radioThemeAmber);
                break;
            case ThemeHelper.THEME_ROSE:
                radioGroupTheme.check(R.id.radioThemeRose);
                break;
            default:
                radioGroupTheme.check(R.id.radioThemeDefault);
                break;
        }

        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            String newTheme = ThemeHelper.THEME_DEFAULT;
            if (checkedId == R.id.radioThemeGreen) newTheme = ThemeHelper.THEME_GREEN;
            else if (checkedId == R.id.radioThemeBlue) newTheme = ThemeHelper.THEME_BLUE;
            else if (checkedId == R.id.radioThemePurple) newTheme = ThemeHelper.THEME_PURPLE;
            else if (checkedId == R.id.radioThemeAmber) newTheme = ThemeHelper.THEME_AMBER;
            else if (checkedId == R.id.radioThemeRose) newTheme = ThemeHelper.THEME_ROSE;

            if (!currentTheme.equals(newTheme)) {
                ThemeHelper.setTheme(requireContext(), newTheme);
                requireActivity().recreate();
            }
        });

        // Appearance Mode (Dark/Light)
        RadioGroup radioGroupAppearance = view.findViewById(R.id.radioGroupAppearance);
        int currentNightMode = ThemeHelper.getNightMode(requireContext());
        if (currentNightMode == ThemeHelper.MODE_DARK) {
            radioGroupAppearance.check(R.id.radioAppDark);
        } else if (currentNightMode == ThemeHelper.MODE_LIGHT) {
            radioGroupAppearance.check(R.id.radioAppLight);
        } else {
            radioGroupAppearance.check(R.id.radioAppSystem);
        }

        radioGroupAppearance.setOnCheckedChangeListener((group, checkedId) -> {
            int newMode = ThemeHelper.MODE_SYSTEM;
            if (checkedId == R.id.radioAppDark) newMode = ThemeHelper.MODE_DARK;
            else if (checkedId == R.id.radioAppLight) newMode = ThemeHelper.MODE_LIGHT;

            if (currentNightMode != newMode) {
                ThemeHelper.setNightMode(requireContext(), newMode);
                // AppCompatDelegate will automatically recreate activities if needed
            }
        });

        RadioGroup radioGroupLanguage = view.findViewById(R.id.radioGroupLanguage);
        
        androidx.core.os.LocaleListCompat currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales();
        if (currentLocale.isEmpty() || currentLocale.get(0).getLanguage().equals("en")) {
            radioGroupLanguage.check(R.id.radioLangEnglish);
        } else {
            radioGroupLanguage.check(R.id.radioLangArabic);
        }

        radioGroupLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLangArabic) {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                        androidx.core.os.LocaleListCompat.forLanguageTags("ar")
                );
            } else {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                        androidx.core.os.LocaleListCompat.forLanguageTags("en")
                );
            }
        });

        return view;
    }
}
