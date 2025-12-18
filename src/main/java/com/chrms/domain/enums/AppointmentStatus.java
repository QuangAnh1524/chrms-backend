package com.chrms.domain.enums;

public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
