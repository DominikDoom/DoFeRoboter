package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;

/**
 * A straight line from one point to another.
 */
public class Line implements PathCommand {

    private final Position2D start;
    private final Position2D end;

    /**
     * Absolute coordinates.
     */
    public Line(Position2D start, Position2D end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Relative coordinates.
     */
    public Line(Position2D start, double x, double y) {
        double deltaX = start.getX() + x;
        double deltaY = start.getY() + y;
        this.start = start;
        this.end = new Position2D(deltaX, deltaY);
    }

    @Override
    public Position3D getNextPos() {
        return new Position3D(end.getX(), end.getY(), true);
    }
}
