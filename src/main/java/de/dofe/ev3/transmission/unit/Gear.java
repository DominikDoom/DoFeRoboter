package de.dofe.ev3.transmission.unit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Gear implements ITransmissionUnit {

    LARGE(36),
    MEDIUM(24),
    SMALL(12);

    private final int size;

    @Override
    public double getSize() {
        return size;
    }
}
