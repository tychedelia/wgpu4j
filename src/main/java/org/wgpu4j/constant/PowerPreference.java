package org.wgpu4j.constant;

/**
 * Preference for power vs performance when selecting a graphics adapter.
 */
public enum PowerPreference {
    /**
     * No preference specified
     */
    UNDEFINED(0),

    /**
     * Prefer low power consumption (integrated GPU)
     */
    LOW_POWER(1),

    /**
     * Prefer high performance (discrete GPU)
     */
    HIGH_PERFORMANCE(2);

    private final int value;

    PowerPreference(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PowerPreference fromValue(int value) {
        for (PowerPreference preference : values()) {
            if (preference.value == value) {
                return preference;
            }
        }
        throw new IllegalArgumentException("Unknown PowerPreference value: " + value);
    }
}