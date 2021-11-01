package de.dofe.ev3.transmission;

/**
 * Interface for calculating transmission ratios.
 * <p>
 * Ex.: Gearbox, touching wheels, belts.
 */
public interface ITransmission {

    /**
     * @return The transmission ratio.
     */
    double getRatio();

    /**
     * @return <i>true</i> if the rotation direction of the powering motor
     * is reversed by the transmission;<p><i>false</i> otherwise.
     */
    boolean isReversing();
}
