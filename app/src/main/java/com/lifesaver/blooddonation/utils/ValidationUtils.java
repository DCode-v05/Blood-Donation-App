package com.lifesaver.blooddonation.utils;

import android.text.TextUtils;
import android.util.Patterns;

public final class ValidationUtils {
    private ValidationUtils() {}

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        String stripped = phone.replaceAll("[\\s\\-()]+", "");
        return stripped.matches("^\\+?[1-9]\\d{0,15}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static String initials(String fullName) {
        if (TextUtils.isEmpty(fullName)) return "";
        String[] parts = fullName.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length && sb.length() < 2; i++) {
            if (!parts[i].isEmpty()) sb.append(parts[i].charAt(0));
        }
        return sb.toString().toUpperCase();
    }
}
