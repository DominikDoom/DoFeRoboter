package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position2D;

/**
 * A move without drawing
 */
public class Move implements PathCommand {

    private Position2D pos;

    /**
     * Absolute move
     */
    public Move(Position2D pos) {
        this.pos = pos;
    }

    /**
     * Relative move.
     */
    public Move(Position2D currentPos, double x, double y) {
        double deltaX = currentPos.getX() + x;
        double deltaY = currentPos.getY() + y;
        this.pos = new Position2D(deltaX, deltaY);
    }

    @Override
    public void execute() {

    }
}
