package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

public class OnboardingInfoFragment extends Fragment {

    private static final String ARG_ICON = "icon";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";

    public static OnboardingInfoFragment newInstance(int imageResId, int titleResId, int descResId) {
        OnboardingInfoFragment fragment = new OnboardingInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ICON, imageResId);
        args.putInt(ARG_TITLE, titleResId);
        args.putInt(ARG_DESC, descResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_onboarding_page, container, false);

        if (getArguments() != null) {
            ImageView imageIcon = view.findViewById(R.id.imgOnboarding);
            TextView textTitle = view.findViewById(R.id.txtTitle);
            TextView textDescription = view.findViewById(R.id.txtDescription);

            int imgResId = getArguments().getInt(ARG_ICON);
            if (imgResId != 0) {
                imageIcon.setImageResource(imgResId);
                
                // Add a gentle floating animation
                ObjectAnimator animator = ObjectAnimator.ofFloat(imageIcon, "translationY", 0f, -20f, 0f);
                animator.setDuration(3000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.start();
            }
            textTitle.setText(getArguments().getInt(ARG_TITLE));
            textDescription.setText(getArguments().getInt(ARG_DESC));
        }

        return view;
    }
}
