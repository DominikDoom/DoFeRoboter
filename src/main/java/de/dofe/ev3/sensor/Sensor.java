package de.dofe.ev3.sensor;

import lejos.hardware.sensor.BaseSensor;
import lejos.hardware.sensor.SensorMode;
import lombok.Getter;
import lombok.Setter;

/**
 * Serves as a wrapper for leJOS {@link BaseSensor} functionality,
 * providing simplified initialisation logic.
 */
public abstract class Sensor<T extends BaseSensor> {

    /**
     * The sample array of the sensor. Its size is defined
     * by the specific {@link SensorMode} implementation.
     */
    private final float[] sample;

    /**
     * The sensor itself.
     */
    @Getter
    private final T sensor;

    /**
     * The {@link SensorMode} the sensor should be initialized with.
     */
    @Getter
    @Setter
    protected SensorMode sensorMode;

    protected Sensor(T sensor) {
        this.sensor = sensor;
        this.sensorMode = this.sensor.getMode(this.sensor.getCurrentMode());
        this.sample = new float[this.sensorMode.sampleSize()];
    }

    /**
     * This assumes the sensor mode only returns a single value.
     * <br>
     * Further values, if present, are not used.
     *
     * @return The first element of the sample array.
     */
    protected float getSample() {
        this.sensorMode.fetchSample(sample, 0);
        return sample[0];
    }

    /**
     * A sensor is active if the current sample fits
     * the criteria of its {@link #isActive(float)} implementation.
     */
    public boolean isActive() {
        return this.isActive(this.getSample());
    }

    /**
     * The isActive method to be implemented by concrete sensors.
     */
    protected abstract boolean isActive(float value);
}
