package com.lifesaver.blooddonation.utils;

import com.lifesaver.blooddonation.constants.BloodGroups;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BloodCompatibility {
    private BloodCompatibility() {}

    public static List<String> compatibleDonors(String recipient) {
        BloodGroups.Info info = BloodGroups.INFO.get(recipient);
        if (info == null) return Collections.emptyList();
        return Arrays.asList(info.canReceiveFrom);
    }

    public static List<String> compatibleRecipients(String donor) {
        BloodGroups.Info info = BloodGroups.INFO.get(donor);
        if (info == null) return Collections.emptyList();
        return Arrays.asList(info.canDonateTo);
    }

    public static boolean canDonate(String donorGroup, String recipientGroup) {
        return compatibleDonors(recipientGroup).contains(donorGroup);
    }
}
