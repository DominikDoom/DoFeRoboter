package de.dofe.ev3.achse;

import de.dofe.ev3.IUebersetzung;
import de.dofe.ev3.motor.Einbaurichtung;
import de.dofe.ev3.reifen.Reifen;
import de.dofe.ev3.sensor.Sensor;
import lejos.hardware.port.Port;

public class MultiPositionAchse extends Achse {


    public MultiPositionAchse(Sensor sensor, Port port, Einbaurichtung einbaurichtung, Reifen antriebsEinheit, IUebersetzung... uebersetzungsEinheiten) {
        super(sensor, port, einbaurichtung, antriebsEinheit, uebersetzungsEinheiten);
    }

    public void backward() {
        this.getMotor().backward();
    }

    public void backward(long timeInMillis) throws InterruptedException {
        this.backward();
        Thread.sleep(timeInMillis);
        this.getMotor().getRegulatedMotor().stop();
    }

    public void forward() {
        this.getMotor().forward();
    }

    public void forward(long timeInMillis) throws InterruptedException {
        this.forward();
        Thread.sleep(timeInMillis);
        this.getMotor().getRegulatedMotor().stop();
    }

    public double getPositionFromTachoCount() {
        final double gearWheelRatio = this.getUebersetzungsEinheiten().get(0).getUebersetzungsverhaeltnis();
        final double umfang = this.antriebsEinheit.getUmfang();
        final int tachoCount = this.getTachoCount();
        double mm = (tachoCount * umfang) / (gearWheelRatio * 360);
        if (this.getMotor().getEinbaurichtung() == Einbaurichtung.UMGEKEHRT)
            mm = mm * -1;
        return mm;
    }

    public int getTachoCount() {
        return this.getMotor().getRegulatedMotor().getTachoCount();
    }

    public void resetTachoCount() {
        this.getMotor().getRegulatedMotor().resetTachoCount();
    }

    public void rotateMm(double mm) {
        int degree = this.mmToDegree(mm);
        if (this.getMotor().getEinbaurichtung().equals(Einbaurichtung.UMGEKEHRT))
            degree *= -1;
        this.getMotor().getRegulatedMotor().rotate(degree);
    }
}
