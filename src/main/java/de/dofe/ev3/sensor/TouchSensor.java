package de.dofe.ev3.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensor extends Sensor<EV3TouchSensor> {

    public TouchSensor(Port port) {
        super(new EV3TouchSensor(port));
        this.sensorMode = this.getSensor().getTouchMode();
    }

    @Override
    protected boolean isActive(float value) {
        return value == 1;
    }
}
