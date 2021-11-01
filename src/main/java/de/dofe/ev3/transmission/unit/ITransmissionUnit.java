package de.dofe.ev3.transmission.unit;

/**
 * Provides a size which is used to calculate transmission ratios.
 * <p>
 * Ex.: Gear teeth, wheel diameter.
 */
public interface ITransmissionUnit {

    /**
     * @return The size of the unit.
     */
    double getSize();
}
