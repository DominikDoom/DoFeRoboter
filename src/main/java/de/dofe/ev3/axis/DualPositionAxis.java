package de.dofe.ev3.axis;

import de.dofe.ev3.motor.Motor;
import lejos.utility.Delay;
import lombok.Builder;
import lombok.Getter;

public class DualPositionAxis extends Axis {

    @Getter
    private boolean active;

    @Builder
    protected DualPositionAxis(Motor motor) {
        super(null, motor, null, null);
    }

    public void activate() {
        if (this.active)
            return;

        toggle();
    }

    public void deactivate() {
        if (!this.active)
            return;

        toggle();
    }

    private void toggle() {
        this.active = !this.active;
        this.getMotor().rotate(this.active ? -90 : 90);
        Delay.msDelay(500);
    }
}
