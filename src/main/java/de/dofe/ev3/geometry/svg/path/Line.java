package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position2D;

/**
 * A straight line from one point to another.
 */
public class Line implements PathCommand {

    private Position2D start;
    private Position2D end;

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
    public void execute() {

    }
}
