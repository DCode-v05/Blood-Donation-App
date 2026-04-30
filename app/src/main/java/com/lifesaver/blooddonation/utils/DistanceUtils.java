package com.lifesaver.blooddonation.utils;

import com.lifesaver.blooddonation.models.GeoLocation;

/**
 * Java port of utils/helpers.js Haversine distance + formatters.
 */
public final class DistanceUtils {
    private DistanceUtils() {}

    private static final double EARTH_RADIUS_KM = 6371.0;

    /** Distance in kilometres, rounded to one decimal. */
    public static double calculate(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(EARTH_RADIUS_KM * c * 10.0) / 10.0;
    }

    public static double calculate(GeoLocation a, GeoLocation b) {
        if (a == null || b == null) return -1;
        return calculate(a.getLatitude(), a.getLongitude(),
                         b.getLatitude(), b.getLongitude());
    }

    public static String format(double km) {
        if (km < 1) return Math.round(km * 1000) + "m";
        if (km < 10) return String.format("%.1fkm", km);
        return Math.round(km) + "km";
    }

    /** Bearing 0–360 degrees from a → b. */
    public static double bearing(GeoLocation a, GeoLocation b) {
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2)
                 - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    public static String direction(double bearing) {
        String[] dirs = {"North", "Northeast", "East", "Southeast",
                         "South", "Southwest", "West", "Northwest"};
        int i = (int) Math.round(bearing / 45.0) % 8;
        return dirs[i];
    }
}
