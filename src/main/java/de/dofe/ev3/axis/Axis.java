package de.dofe.ev3.axis;

import de.dofe.ev3.motor.Motor;
import de.dofe.ev3.sensor.Sensor;
import de.dofe.ev3.transmission.ITransmission;
import de.dofe.ev3.transmission.unit.Wheel;
import lejos.hardware.sensor.BaseSensor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Singular;

@Getter
@AllArgsConstructor
public abstract class Axis {

    protected final Wheel driveUnit;
    private final Motor motor;
    private final Sensor<? extends BaseSensor> sensor;

    @Singular
    private final ITransmission transmissionUnits;

    public void setSpeed(double mmSecond) {
        this.motor.setSpeed(this.mmToDegree(mmSecond));
    }

    protected int mmToDegree(double mmSpeed) {
        // Bsp.: 10mm, 30mm, toMove = 10/30 = 0.3, 0.3 / 0.5 = 0.6 --> 360*0,6 = 216
        double toMove = mmSpeed / driveUnit.getCircumference() * transmissionUnits.getRatio();
        return (int) (toMove * 360);
    }
}
