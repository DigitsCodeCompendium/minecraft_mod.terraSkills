package com.terraskills.progression;

import java.util.Locale;
import java.util.Optional;

public enum RpgStat {
    MIGHT, FINESSE, ENDURANCE, INTELLIGENCE, INSTINCT;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<RpgStat> parse(String value) {
        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
