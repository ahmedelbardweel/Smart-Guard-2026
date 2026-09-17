package com.example.myapplication;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingFragmentAdapter extends FragmentStateAdapter {

    private final Context context;

    public OnboardingFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.context = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OnboardingLanguageFragment();
            case 1:
                return new OnboardingAppearanceFragment();
            case 2:
                return new OnboardingThemeFragment();
            case 3:
                return OnboardingInfoFragment.newInstance(
                        R.drawable.ic_onboarding_home,
                        R.string.onboarding_title_1,
                        R.string.onboarding_desc_1
                );
            case 4:
                return OnboardingInfoFragment.newInstance(
                        R.drawable.ic_onboarding_bluetooth,
                        R.string.onboarding_title_2,
                        R.string.onboarding_desc_2
                );
            case 5:
                return OnboardingInfoFragment.newInstance(
                        R.drawable.ic_onboarding_analytics,
                        R.string.onboarding_title_3,
                        R.string.onboarding_desc_3
                );
            case 6:
                return OnboardingInfoFragment.newInstance(
                        R.drawable.ic_onboarding_logs,
                        R.string.onboarding_title_4,
                        R.string.onboarding_desc_4
                );
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
