package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {
    private static final String PREFS = "theme_prefs";
    private static final String KEY_THEME = "app_theme";
    private static final String KEY_NIGHT_MODE = "night_mode";

    public static final String THEME_DEFAULT = "default";
    public static final String THEME_GREEN = "green";
    public static final String THEME_BLUE = "blue";
    public static final String THEME_PURPLE = "purple";
    public static final String THEME_AMBER = "amber";
    public static final String THEME_ROSE = "rose";

    public static final int MODE_SYSTEM = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int MODE_LIGHT = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
    public static final int MODE_DARK = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

    public static void setNightMode(Context context, int mode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_NIGHT_MODE, mode);
        editor.apply();
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode);
    }

    public static int getNightMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_NIGHT_MODE, MODE_LIGHT);
    }

    public static void applyNightMode(Context context) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(getNightMode(context));
    }

    public static void setTheme(Context context, String theme) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_THEME, theme);
        editor.apply();
    }

    public static String getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_THEME, THEME_ROSE);
    }

    public static void applyTheme(Context context) {
        String theme = getTheme(context);
        switch (theme) {
            case THEME_GREEN:
                context.setTheme(R.style.Theme_MyApplication_Green);
                break;
            case THEME_BLUE:
                context.setTheme(R.style.Theme_MyApplication_Blue);
                break;
            case THEME_PURPLE:
                context.setTheme(R.style.Theme_MyApplication_Purple);
                break;
            case THEME_AMBER:
                context.setTheme(R.style.Theme_MyApplication_Amber);
                break;
            case THEME_ROSE:
                context.setTheme(R.style.Theme_MyApplication_Rose);
                break;
            default:
                context.setTheme(R.style.Theme_MyApplication);
                break;
        }
    }
}
