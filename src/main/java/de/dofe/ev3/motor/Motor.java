package de.dofe.ev3.motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;
import lombok.Getter;

public class Motor {

    @Getter
    private final RegulatedMotor regulatedMotor;
    @Getter
    private final Einbaurichtung einbaurichtung;

    public Motor(Port port, Einbaurichtung einbaurichtung) {
        this.regulatedMotor = new EV3LargeRegulatedMotor(port);
        this.einbaurichtung = einbaurichtung;
    }

    public void backward() {
        if (this.einbaurichtung == Einbaurichtung.REGULAER)
            this.regulatedMotor.backward();
        else
            this.regulatedMotor.forward();
    }

    public void forward() {
        if (this.einbaurichtung == Einbaurichtung.REGULAER)
            this.regulatedMotor.forward();
        else
            this.regulatedMotor.backward();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.regulatedMotor.close();
    }
}
