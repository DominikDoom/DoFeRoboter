package de.dofe.ev3.motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lombok.Getter;

/**
 * Wrapper for the leJOS {@link EV3LargeRegulatedMotor}.
 * <p>
 * Takes the motor {@link MountDirection} into account
 * for matching visual with logic directions.
 */
public class Motor extends EV3LargeRegulatedMotor {

    /**
     * The {@link MountDirection} of the motor.
     */
    @Getter
    private final MountDirection mountDirection;

    /**
     * Initializes the motor.
     *
     * @param port           The {@link MotorPort} the motor is connected to.
     * @param mountDirection The direction the motor is mounted in.
     */
    public Motor(Port port, MountDirection mountDirection) {
        super(port);
        this.mountDirection = mountDirection;
    }

    /**
     * Moves the motor backward.
     * Automatically adjusts to {@link #mountDirection}.
     */
    @Override
    public void backward() {
        if (this.mountDirection == MountDirection.REGULAR)
            super.backward();
        else
            super.forward();
    }

    /**
     * Moves the motor forward.
     * Automatically adjusts to {@link #mountDirection}.
     */
    @Override
    public void forward() {
        if (this.mountDirection == MountDirection.REGULAR)
            super.forward();
        else
            super.backward();
    }
}
