package de.dofe.ev3.axis;

import de.dofe.ev3.motor.Motor;
import de.dofe.ev3.motor.MountDirection;
import de.dofe.ev3.sensor.Sensor;
import de.dofe.ev3.transmission.ITransmission;
import de.dofe.ev3.transmission.unit.Wheel;
import lejos.hardware.sensor.BaseSensor;
import lejos.utility.Delay;
import lombok.Builder;

/**
 * An {@link Axis} with free range of movement in a given direction.
 * <p>
 * In the Plott3r setup, it is used for the X and Y axes moving the belt and the paper.
 */
public class MultiPositionAxis extends Axis {

    @Builder
    protected MultiPositionAxis(Wheel driveUnit, Motor motor, Sensor<? extends BaseSensor> sensor, ITransmission transmissionUnits) {
        super(driveUnit, motor, sensor, transmissionUnits);
    }

    /**
     * Moves the axis backward for the specified time.
     *
     * @param timeInMillis The time in milliseconds.
     */
    public void backward(long timeInMillis) {
        this.getMotor().backward();
        Delay.msDelay(timeInMillis);
        this.getMotor().stop();
    }

    /**
     * Moves the axis forward for the specified time.
     *
     * @param timeInMillis The time in milliseconds.
     */
    public void forward(long timeInMillis) {
        this.getMotor().forward();
        Delay.msDelay(timeInMillis);
        this.getMotor().stop();
    }

    /**
     * Get the current position on the axis by using the tacho count
     * (degrees of motor rotation since start / reset) to calculate the
     * offset from the start.
     */
    public double getPositionFromTachoCount() {
        final double gearWheelRatio = this.getTransmissionUnits().getRatio();
        final double circumference = this.driveUnit.getCircumference();
        final int tachoCount = this.getMotor().getTachoCount();

        double mm = (tachoCount * circumference) / (gearWheelRatio * 360);
        return this.getMotor().getMountDirection() == MountDirection.REVERSE ? mm * -1 : mm;
    }

    /**
     * Rotate the axis for a total distance.
     *
     * @param mm The distance to rotate in mm
     */
    public void rotateMm(double mm) {
        boolean inverse = this.getMotor().getMountDirection().equals(MountDirection.REVERSE);

        int degree = inverse ? this.mmToDegree(mm) * -1 : this.mmToDegree(mm);
        this.getMotor().rotate(degree);
    }
}
