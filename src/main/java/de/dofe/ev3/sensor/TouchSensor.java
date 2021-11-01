package de.dofe.ev3.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;

public class TouchSensor extends Sensor<EV3TouchSensor> {

    public TouchSensor(Port port) {
        super(new EV3TouchSensor(port));
    }

    @Override
    protected SensorMode getSensorMode(EV3TouchSensor sensor) {
        return sensor.getTouchMode();
    }

    @Override
    protected boolean isAktiv(float wert) {
        return wert == 1;
    }
}
