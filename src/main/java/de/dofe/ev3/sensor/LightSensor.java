package de.dofe.ev3.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class LightSensor extends Sensor<EV3ColorSensor> {

    private static final float EPSILON = 0.02f;
    private final float threshold;

    public LightSensor(Port port) {
        super(new EV3ColorSensor(port));
        this.sensorMode = this.getSensor().getRedMode();

        this.threshold = this.getSample();
    }

    @Override
    protected boolean isActive(float value) {
        return Math.abs(value - threshold) > EPSILON;
    }
}
