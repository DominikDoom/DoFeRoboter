package de.dofe.ev3.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * A wrapper for the leJOS {@link EV3ColorSensor}.
 * <p>
 * Instead of using the <i>ambient</i> sensor mode,
 * the lightness value is interpreted from the red channel.
 */
public class LightSensor extends Sensor<EV3ColorSensor> {

    /**
     * The upper bound for error tolerance.
     */
    private static final float EPSILON = 0.02f;
    /**
     * The threshold for interpreting the sensor as active.
     */
    private final float threshold;

    /**
     * Initializes the sensor to <i>red</i> mode.
     *
     * @param port The {@link SensorPort} the sensor is connected to.
     */
    public LightSensor(Port port) {
        super(new EV3ColorSensor(port));
        this.sensorMode = this.getSensor().getRedMode();

        this.threshold = this.getSample();
    }

    /**
     * The sensor is active if the current red value exceeds
     * the {@link #threshold} by more than {@link #EPSILON}.
     */
    @Override
    protected boolean isActive(float value) {
        return Math.abs(value - threshold) > EPSILON;
    }
}
