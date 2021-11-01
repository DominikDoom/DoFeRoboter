package de.dofe.ev3.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class LichtSensor extends Sensor<EV3ColorSensor>{

    private static final float EPSILON = 0.02f;
    private float schwellwert;

    public LichtSensor(Port port) {
        super(new EV3ColorSensor(port));
        this.schwellwert = this.getWert();
    }

    @Override
    protected SensorMode getSensorMode(EV3ColorSensor sensor) {
        return sensor.getRedMode();
    }

    @Override
    protected boolean isAktiv(float wert) {
        return Math.abs(wert - schwellwert) > EPSILON;
    }
}
