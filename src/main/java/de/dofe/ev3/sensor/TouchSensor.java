package de.dofe.ev3.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

/**
 * A wrapper for the leJOS {@link EV3TouchSensor}.
 */
public class TouchSensor extends Sensor<EV3TouchSensor> {

    /**
     * Initializes the sensor to <i>touch</i> mode.
     *
     * @param port The {@link SensorPort} the sensor is connected to.
     */
    public TouchSensor(Port port) {
        super(new EV3TouchSensor(port));
        this.sensorMode = this.getSensor().getTouchMode();
    }

    /**
     * Since the sensor only has two states, it is always active if
     * the sample is 1.
     */
    @Override
    protected boolean isActive(float value) {
        return value == 1;
    }
}
