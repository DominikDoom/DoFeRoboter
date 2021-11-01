package de.dofe.ev3.sensor;

import lejos.hardware.sensor.BaseSensor;
import lejos.hardware.sensor.SensorMode;

public abstract class Sensor<T extends BaseSensor> {

    private final float[] sample;
    private final T sensor;
    private final SensorMode sensorMode;

    protected Sensor(T sensor) {
        this.sensor = sensor;
        this.sensorMode = this.sensor.getMode(this.sensor.getCurrentMode());
        this.sample = new float[this.sensorMode.sampleSize()];
    }

    public void close() {
        this.sensor.close();
    }

    protected T getSensor() {
        return this.sensor;
    }

    protected abstract SensorMode getSensorMode(T sensor);

    protected float getWert() {
        this.sensorMode.fetchSample(sample, 0);
        return sample[0];
    }

    public boolean isAktiv() {
        return this.isAktiv(this.getWert());
    }

    protected abstract boolean isAktiv(float wert);
}
