package de.dofe.ev3.achse;

import de.dofe.ev3.motor.Einbaurichtung;
import de.dofe.ev3.reifen.Reifen;
import de.dofe.ev3.sensor.Sensor;
import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lombok.Getter;

public class DualPositionAchse extends Achse {

    @Getter
    private boolean aktiv;

    public DualPositionAchse(Sensor sensor, Port port, Einbaurichtung einbaurichtung, Reifen antriebsEinheit) {
        super(sensor, port, einbaurichtung, antriebsEinheit, null);
    }

    public void aktiviere() {
        if (this.aktiv)
            return;
        this.aktiv = true;
        this.getMotor().getRegulatedMotor().rotate(-90);
        Delay.msDelay(500);
    }

    public void deaktiviere() {
        if (!this.aktiv)
            return;
        this.aktiv = false;
        this.getMotor().getRegulatedMotor().rotate(90);
        Delay.msDelay(500);
    }
}
