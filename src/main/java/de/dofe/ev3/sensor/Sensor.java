package de.dofe.ev3.sensor;

import lejos.hardware.sensor.BaseSensor;
import lejos.hardware.sensor.SensorMode;
import lombok.Getter;
import lombok.Setter;

public abstract class Sensor<T extends BaseSensor> {

    private final float[] sample;
    @Getter
    private final T sensor;
    @Getter
    @Setter
    protected SensorMode sensorMode;

    protected Sensor(T sensor) {
        this.sensor = sensor;
        this.sensorMode = this.sensor.getMode(this.sensor.getCurrentMode());
        this.sample = new float[this.sensorMode.sampleSize()];
    }

    protected float getSample() {
        this.sensorMode.fetchSample(sample, 0);
        return sample[0];
    }

    public boolean isActive() {
        return this.isActive(this.getSample());
    }

    protected abstract boolean isActive(float value);
}
