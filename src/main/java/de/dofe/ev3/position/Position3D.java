package de.dofe.ev3.position;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class Position3D extends Position2D {

    @Getter
    private final boolean z;

    public Position3D(Position2D position2D, boolean z) {
        super(position2D.getX(), position2D.getY());
        this.z = z;
    }

    public Position3D(double x, double y, boolean z) {
        super(x, y);
        this.z = z;
    }
}
