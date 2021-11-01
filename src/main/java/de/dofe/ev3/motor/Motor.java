package de.dofe.ev3.motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lombok.Getter;

public class Motor extends EV3LargeRegulatedMotor {

    @Getter
    private final MountDirection mountDirection;

    public Motor(Port port, MountDirection mountDirection) {
        super(port);
        this.mountDirection = mountDirection;
    }

    @Override
    public void backward() {
        if (this.mountDirection == MountDirection.REGULAR)
            super.backward();
        else
            super.forward();
    }

    @Override
    public void forward() {
        if (this.mountDirection == MountDirection.REGULAR)
            super.forward();
        else
            super.backward();
    }
}
