package de.dofe.ev3.position;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Extends {@link Position2D} with a Z axis.
 * <p>
 * Since the current implementation only moves on the Z axis
 * to two possible positions (completely up or down), the Z position
 * is not a coordinate value but a boolean.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class Position3D extends Position2D {

    /**
     * Represents the position state of the z axis.
     * <ul>
     *     <li>false = up
     *     <li>true = down
     * </ul>
     */
    @Getter
    private final boolean z;

    /**
     * Constructs a {@link Position3D} using an existing {@link Position2D}.
     *
     * @param position2D The 2D position.
     * @param z          Z axis up (false) or down (true)
     */
    public Position3D(Position2D position2D, boolean z) {
        super(position2D.getX(), position2D.getY());
        this.z = z;
    }

    /**
     * Constructs a {@link Position3D} using coordinates.
     *
     * @param x The x coordinate of the position.
     * @param y The y coordinate of the position.
     * @param z Z axis up (false) or down (true).
     */
    public Position3D(double x, double y, boolean z) {
        super(x, y);
        this.z = z;
    }
}
