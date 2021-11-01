package de.dofe.ev3.axis;

import de.dofe.ev3.motor.Motor;
import de.dofe.ev3.sensor.Sensor;
import de.dofe.ev3.transmission.ITransmission;
import de.dofe.ev3.transmission.unit.Wheel;
import lejos.hardware.sensor.BaseSensor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Singular;

/**
 * Provides basic functionality for movement on an axis.
 */
@Getter
@AllArgsConstructor
public abstract class Axis {

    /**
     * The {@link Wheel} rotating the axis.
     */
    protected final Wheel driveUnit;
    /**
     * The {@link Motor} powering the axis.
     */
    private final Motor motor;
    /**
     * The {@link Sensor} used to initialize / reset the position on the axis.
     */
    private final Sensor<? extends BaseSensor> sensor;

    /**
     * The transmission between {@link #motor} and {@link #driveUnit}.
     */
    @Singular
    private final ITransmission transmissionUnits;

    /**
     * Sets the speed for the {@link #motor} of the axis.
     *
     * @param mmSecond The speed in mm/s.
     */
    public void setSpeed(double mmSecond) {
        this.motor.setSpeed(this.mmToDegree(mmSecond));
    }

    /**
     * Converts a speed provided in mm/s to deg/s
     * using the axis' drive wheel circumference and gear transmission ratio.
     * <p>
     * Ex.:
     * <p>
     * Speed = 10 mm/s<br>
     * Drive wheel circumference = 125 mm<br>
     * Gear ratio = 36 teeth / 12 teeth = 3
     * <pre>
     * {@code
     * toMove = mmSpeed / circumference * ratio
     * = 10 / 125 * 3
     * = 0.24;
     *
     * degSpeed = toMove * 360
     * = 86.4 deg/s;}</pre>
     */
    protected int mmToDegree(double mmSpeed) {
        double toMove = mmSpeed / driveUnit.getCircumference() * transmissionUnits.getRatio();
        return (int) (toMove * 360);
    }
}
