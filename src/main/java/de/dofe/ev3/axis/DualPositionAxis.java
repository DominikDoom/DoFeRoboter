package de.dofe.ev3.axis;

import de.dofe.ev3.motor.Motor;
import lejos.utility.Delay;
import lombok.Builder;
import lombok.Getter;

/**
 * An {@link Axis} with only two positions. In the Plott3r setup,
 * it is either up or down, moving by a fixed amount.
 */
public class DualPositionAxis extends Axis {

    /**
     * Whether the axis is up (false) or down (true)
     */
    @Getter
    private boolean active;

    /**
     * This axis is directly connected to the motor and needs no drive, transmission or sensor.
     */
    @Builder
    protected DualPositionAxis(Motor motor) {
        super(null, motor, null, null);
    }

    /**
     * Activates (lowers) the axis.
     * <p>
     * Does nothing if it is already active.
     */
    public void activate() {
        if (this.active)
            return;

        toggle();
    }

    /**
     * Deactivates (raises) the axis.
     * <p>
     * Does nothing if it is already inactive.
     */
    public void deactivate() {
        if (!this.active)
            return;

        toggle();
    }

    /**
     * Moves the axis either up or down by a fixed amount,
     * depending on the current state.
     */
    public void toggle() {
        this.active = !this.active;
        this.getMotor().rotate(this.active ? -90 : 90);
        Delay.msDelay(500);
    }
}
