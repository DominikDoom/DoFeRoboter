package de.dofe.ev3.transmission.unit;

import lombok.Data;

@Data
public class Wheel implements ITransmissionUnit {

    double diameter;
    double circumference;

    public Wheel(double diameter) {
        this.diameter = diameter;
        this.circumference = 2 * Math.PI * (this.diameter / 2);
    }

    @Override
    public double getSize() {
        return this.diameter;
    }
}
