package de.dofe.ev3.axis;

import de.dofe.ev3.motor.Motor;
import de.dofe.ev3.motor.MountDirection;
import de.dofe.ev3.sensor.Sensor;
import de.dofe.ev3.transmission.ITransmission;
import de.dofe.ev3.transmission.unit.Wheel;
import lejos.hardware.sensor.BaseSensor;
import lejos.utility.Delay;
import lombok.Builder;

public class MultiPositionAxis extends Axis {

    @Builder
    public MultiPositionAxis(Wheel driveUnit, Motor motor, Sensor<? extends BaseSensor> sensor, ITransmission transmissionUnits) {
        super(driveUnit, motor, sensor, transmissionUnits);
    }

    public void backward(long timeInMillis) {
        this.getMotor().backward();
        Delay.msDelay(timeInMillis);
        this.getMotor().stop();
    }

    public void forward(long timeInMillis) {
        this.getMotor().forward();
        Delay.msDelay(timeInMillis);
        this.getMotor().stop();
    }

    public double getPositionFromTachoCount() {
        final double gearWheelRatio = this.getTransmissionUnits().getRatio();
        final double circumference = this.driveUnit.getCircumference();
        final int tachoCount = this.getMotor().getTachoCount();

        double mm = (tachoCount * circumference) / (gearWheelRatio * 360);
        return this.getMotor().getMountDirection() == MountDirection.REVERSE ? mm * -1 : mm;
    }

    public void rotateMm(double mm) {
        boolean inverse = this.getMotor().getMountDirection().equals(MountDirection.REVERSE);

        int degree = inverse ? this.mmToDegree(mm) * -1 : this.mmToDegree(mm);
        this.getMotor().rotate(degree);
    }
}
