package de.dofe.ev3.achse;

import de.dofe.ev3.IUebersetzung;
import de.dofe.ev3.motor.Einbaurichtung;
import de.dofe.ev3.motor.Motor;
import de.dofe.ev3.reifen.Reifen;
import de.dofe.ev3.sensor.Sensor;
import lejos.hardware.port.Port;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Getter
public abstract class Achse {

    protected final Reifen antriebsEinheit;
    private final Motor motor;
    private final Sensor sensor;
    private final ArrayList<IUebersetzung> uebersetzungsEinheiten = new ArrayList<>();

    protected Achse(Sensor sensor, Port port, Einbaurichtung einbaurichtung, Reifen antriebsEinheit, IUebersetzung... uebersetzungsEinheiten) {
        this.sensor = sensor;
        this.motor = new Motor(port, einbaurichtung);
        this.antriebsEinheit = antriebsEinheit;
        if (Objects.nonNull(uebersetzungsEinheiten)) {
            this.uebersetzungsEinheiten.addAll(Arrays.asList(uebersetzungsEinheiten));
        }
    }

    public void setSpeedMM(double mmSecond) {
        this.motor.getRegulatedMotor().setSpeed(this.mmToDegree(mmSecond));
    }

    protected int mmToDegree(double mmSpeed) {
        // Bsp.: 10mm, 30mm, toMove = 10/30 = 0.3, 0.3 / 0.5 = 0.6 --> 360*0,6 = 216
        double toMove = mmSpeed / antriebsEinheit.getUmfang();
        toMove *= uebersetzungsEinheiten.get(0).getUebersetzungsverhaeltnis();
        return (int) (toMove * 360);
    }
}
