package de.dofe.ev3.zahnrad;

import lombok.Getter;

public enum Zahnrad {
    GROSS(36),
    MITTEL(24),
    KLEIN(12);

    @Getter
    private final int size;

    private Zahnrad(int size) {
        this.size = size;
    }
}
