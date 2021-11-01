package de.dofe.ev3.transmission.unit;

import lombok.Getter;

/**
 * Contains diameter & circumference for
 * transmission ratio and distance calculations.
 */
@Getter
public class Wheel implements ITransmissionUnit {

    private final double diameter;
    private final double circumference;

    /**
     * Creates a wheel by diameter.
     * <p>
     * Circumference is set automatically.
     */
    public Wheel(double diameter) {
        this.diameter = diameter;
        this.circumference = 2 * Math.PI * (this.diameter / 2);
    }

    /**
     * @return The wheel diameter.
     */
    @Override
    public double getSize() {
        return this.diameter;
    }
}
