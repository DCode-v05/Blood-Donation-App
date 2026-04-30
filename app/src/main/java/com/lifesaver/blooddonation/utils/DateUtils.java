package com.lifesaver.blooddonation.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private DateUtils() {}

    public static final SimpleDateFormat ISO =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat HUMAN =
            new SimpleDateFormat("MMM d, yyyy", Locale.US);

    public static String today() { return ISO.format(new Date()); }

    public static String format(Date date) {
        return date == null ? "" : HUMAN.format(date);
    }

    public static String formatIso(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            Date d = ISO.parse(iso);
            return d == null ? iso : HUMAN.format(d);
        } catch (ParseException e) {
            return iso;
        }
    }

    public static String timeAgo(String iso) {
        if (iso == null) return "";
        try {
            Date d = ISO.parse(iso);
            if (d == null) return "";
            long diff = (System.currentTimeMillis() - d.getTime()) / 1000;
            if (diff < 60) return "Just now";
            long minutes = diff / 60;
            if (minutes < 60) return minutes + " min ago";
            long hours = minutes / 60;
            if (hours < 24) return hours + " hr ago";
            long days = hours / 24;
            if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";
            return formatIso(iso);
        } catch (ParseException e) {
            return iso;
        }
    }

    public static String addDays(String iso, int days) {
        try {
            Date d = ISO.parse(iso);
            if (d == null) return iso;
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DAY_OF_YEAR, days);
            return ISO.format(c.getTime());
        } catch (ParseException e) {
            return iso;
        }
    }
}
