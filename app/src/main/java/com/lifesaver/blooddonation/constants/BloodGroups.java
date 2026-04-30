package com.lifesaver.blooddonation.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Port of constants/bloodGroups.js — blood-group metadata, donation types,
 * donation requirements, deferral periods, facts and myth-vs-fact entries.
 */
public final class BloodGroups {
    private BloodGroups() {}

    public static final List<String> ALL = Collections.unmodifiableList(
            Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));

    public static class Info {
        public final String name;
        public final String[] canDonateTo;
        public final String[] canReceiveFrom;
        public final String frequency;
        public final String description;

        Info(String name, String[] donate, String[] receive,
             String frequency, String description) {
            this.name = name;
            this.canDonateTo = donate;
            this.canReceiveFrom = receive;
            this.frequency = frequency;
            this.description = description;
        }
    }

    public static final Map<String, Info> INFO;
    static {
        Map<String, Info> m = new HashMap<>();
        m.put("A+",  new Info("A Positive",
                new String[]{"A+","AB+"},
                new String[]{"A+","A-","O+","O-"}, "34%",
                "Second most common blood type"));
        m.put("A-",  new Info("A Negative",
                new String[]{"A+","A-","AB+","AB-"},
                new String[]{"A-","O-"}, "6%",
                "Can donate to both A+ and A- recipients"));
        m.put("B+",  new Info("B Positive",
                new String[]{"B+","AB+"},
                new String[]{"B+","B-","O+","O-"}, "9%",
                "Less common but important blood type"));
        m.put("B-",  new Info("B Negative",
                new String[]{"B+","B-","AB+","AB-"},
                new String[]{"B-","O-"}, "2%",
                "Rare blood type, high demand"));
        m.put("AB+", new Info("AB Positive",
                new String[]{"AB+"},
                new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"}, "3%",
                "Universal plasma donor, can receive from all"));
        m.put("AB-", new Info("AB Negative",
                new String[]{"AB+","AB-"},
                new String[]{"A-","B-","AB-","O-"}, "1%",
                "Rarest blood type"));
        m.put("O+",  new Info("O Positive",
                new String[]{"A+","B+","AB+","O+"},
                new String[]{"O+","O-"}, "38%",
                "Most common blood type"));
        m.put("O-",  new Info("O Negative",
                new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"},
                new String[]{"O-"}, "7%",
                "Universal donor, can donate to all"));
        INFO = Collections.unmodifiableMap(m);
    }

    public static class DonationType {
        public final String value;
        public final String label;
        public final String description;
        public final String duration;
        public final int    intervalDays;
        public final String volume;

        public DonationType(String value, String label, String description,
                            String duration, int intervalDays, String volume) {
            this.value = value;
            this.label = label;
            this.description = description;
            this.duration = duration;
            this.intervalDays = intervalDays;
            this.volume = volume;
        }
    }

    public static final List<DonationType> DONATION_TYPES = Collections.unmodifiableList(Arrays.asList(
            new DonationType("whole_blood", "Whole Blood",
                    "Complete blood donation including all components",
                    "8-10 minutes",   56,  "450ml"),
            new DonationType("platelets",   "Platelets",
                    "Only platelets are collected, other components returned",
                    "90-120 minutes", 7,   "200-400ml"),
            new DonationType("plasma",      "Plasma",
                    "Only plasma is collected, other components returned",
                    "60-90 minutes",  28,  "600-880ml"),
            new DonationType("red_cells",   "Red Blood Cells",
                    "Double red cell donation using apheresis",
                    "25-35 minutes",  112, "2 units")));

    public static final String[] BLOOD_FACTS = {
            "One blood donation can save up to three lives",
            "The human body contains about 10-12 pints of blood",
            "Blood makes up about 7% of your body weight",
            "Red blood cells live for about 120 days",
            "Your body replaces donated blood within 24-48 hours",
            "Type O negative is the universal donor blood type",
            "Type AB positive is the universal plasma donor",
            "Only 3% of age-eligible people donate blood yearly",
            "Blood cannot be manufactured - it can only come from donors",
            "Every 2 seconds someone in the world needs blood"
    };

    public static class MythFact {
        public final String myth;
        public final String fact;

        public MythFact(String myth, String fact) {
            this.myth = myth;
            this.fact = fact;
        }
    }

    public static final List<MythFact> MYTHS = Collections.unmodifiableList(Arrays.asList(
            new MythFact("Blood donation makes you weak",
                    "Your body quickly replenishes donated blood. Most people feel normal within hours."),
            new MythFact("You can get diseases from donating blood",
                    "All equipment is sterile and used only once. There is no risk of infection."),
            new MythFact("Vegetarians cannot donate blood",
                    "Vegetarians can donate as long as they maintain adequate iron levels."),
            new MythFact("You need to fast before donating",
                    "You should eat a healthy meal before donating to maintain blood sugar levels."),
            new MythFact("Blood donation takes too much time",
                    "The actual donation takes only 8-10 minutes; total process is about 45-60 minutes.")));
}
