package com.example.movieapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    private static final String KEY_IMAGE_URL = "image_url";
    private static final String PREFERENCE_NAME = "user_preferences";
    private static final String KEY_DISPLAY_NAME = "display_name";

    private static final String KEY_WISHLIST_STATUS = "WishListStatus";

    public static void saveDisplayName(Context context, String displayName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_DISPLAY_NAME, displayName);
        editor.apply();
    }

    public static String getDisplayName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_DISPLAY_NAME, null);
    }
    public static void saveImageUrl(Context context, String imageUrl) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_IMAGE_URL, imageUrl);
        editor.apply();
    }

    public static String getImageUrl(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_IMAGE_URL, null);
    }
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setWishListStatus(Context context, int movieId, boolean status) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_WISHLIST_STATUS + movieId, status);
        editor.apply();
    }

    public static boolean getWishListStatus(Context context, int movieId) {
        return getSharedPreferences(context).getBoolean(KEY_WISHLIST_STATUS + movieId, false);
    }
    public static void removeWishListStatus(Context context, int movieId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_WISHLIST_STATUS + movieId);
        editor.apply();
    }
}
