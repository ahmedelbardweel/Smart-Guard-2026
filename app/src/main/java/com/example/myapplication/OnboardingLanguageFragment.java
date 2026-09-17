package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class OnboardingLanguageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_language, container, false);

        MaterialCardView cardEn = view.findViewById(R.id.cardLangEn);
        MaterialCardView cardAr = view.findViewById(R.id.cardLangAr);

        androidx.core.os.LocaleListCompat currentLocale = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales();
        boolean isArabic = !currentLocale.isEmpty() && currentLocale.get(0).getLanguage().equals("ar");

        updateSelection(cardEn, cardAr, isArabic);

        cardEn.setOnClickListener(v -> {
            if (isArabic) {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                        androidx.core.os.LocaleListCompat.forLanguageTags("en")
                );
            }
        });

        cardAr.setOnClickListener(v -> {
            if (!isArabic) {
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                        androidx.core.os.LocaleListCompat.forLanguageTags("ar")
                );
            }
        });

        return view;
    }

    private void updateSelection(MaterialCardView cardEn, MaterialCardView cardAr, boolean isArabic) {
        int activeStroke = (int) (4 * getResources().getDisplayMetrics().density);
        int inactiveStroke = (int) (1 * getResources().getDisplayMetrics().density);

        if (isArabic) {
            cardAr.setChecked(true);
            cardAr.setStrokeWidth(activeStroke);
            cardEn.setChecked(false);
            cardEn.setStrokeWidth(inactiveStroke);
        } else {
            cardEn.setChecked(true);
            cardEn.setStrokeWidth(activeStroke);
            cardAr.setChecked(false);
            cardAr.setStrokeWidth(inactiveStroke);
        }
    }
}
