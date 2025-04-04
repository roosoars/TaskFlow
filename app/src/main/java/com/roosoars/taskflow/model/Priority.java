package com.roosoars.taskflow.model;

/**
 * Enum representing task priority levels
 */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW;

    public static Priority fromInt(int value) {
        switch (value) {
            case 0:
                return HIGH;
            case 1:
                return MEDIUM;
            case 2:
                return LOW;
            default:
                return MEDIUM;
        }
    }

    public int toInt() {
        switch (this) {
            case HIGH:
                return 0;
            case MEDIUM:
                return 1;
            case LOW:
                return 2;
            default:
                return 1;
        }
    }
}