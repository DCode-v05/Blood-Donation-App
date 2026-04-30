package com.lifesaver.blooddonation.utils;

import com.lifesaver.blooddonation.models.Donation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Computes how soon a donor can donate again, by donation type.
 * Mirrors calculateDonationEligibility() from utils/helpers.js.
 */
public final class DonationEligibility {
    private DonationEligibility() {}

    public static class Result {
        public final boolean eligible;
        public final Date    nextEligibleDate;     // null if eligible now
        public final long    daysSinceLastDonation; // -1 if never donated
        public final long    daysUntilEligible;     // 0 if eligible

        Result(boolean eligible, Date nextEligibleDate,
               long daysSince, long daysUntil) {
            this.eligible = eligible;
            this.nextEligibleDate = nextEligibleDate;
            this.daysSinceLastDonation = daysSince;
            this.daysUntilEligible = daysUntil;
        }
    }

    private static final SimpleDateFormat ISO_DATE =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static int intervalFor(String donationType) {
        if (donationType == null) return 56;
        switch (donationType) {
            case Donation.TYPE_PLATELETS: return 7;
            case Donation.TYPE_PLASMA:    return 28;
            case Donation.TYPE_RED_CELLS: return 112;
            case Donation.TYPE_WHOLE_BLOOD:
            default:                      return 56;
        }
    }

    public static Result compute(String lastDonationDate, String donationType) {
        if (lastDonationDate == null || lastDonationDate.isEmpty()) {
            return new Result(true, null, -1, 0);
        }

        Date last;
        try {
            last = ISO_DATE.parse(lastDonationDate);
        } catch (ParseException e) {
            return new Result(true, null, -1, 0);
        }
        if (last == null) return new Result(true, null, -1, 0);

        long now = System.currentTimeMillis();
        long daysSince = (now - last.getTime()) / (1000L * 60 * 60 * 24);

        int interval = intervalFor(donationType);
        boolean eligible = daysSince >= interval;

        Date next = null;
        long daysUntil = 0;
        if (!eligible) {
            Calendar c = Calendar.getInstance();
            c.setTime(last);
            c.add(Calendar.DAY_OF_YEAR, interval);
            next = c.getTime();
            daysUntil = interval - daysSince;
        }
        return new Result(eligible, next, daysSince, daysUntil);
    }
}
