package de.dofe.ev3.transmission.unit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Provides default Lego gear sizes by tooth number.
 * <ul>
 *     <li>LARGE, 36 teeth
 *     <li>MEDIUM, 24 teeth
 *     <li>SMALL, 12 teeth
 * </ul>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Gear implements ITransmissionUnit {

    LARGE(36),
    MEDIUM(24),
    SMALL(12);

    private final int size;

    /**
     * @return The gear tooth amount.
     */
    @Override
    public double getSize() {
        return size;
    }
}
