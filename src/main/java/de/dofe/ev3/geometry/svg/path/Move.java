package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import lombok.AllArgsConstructor;

/**
 * A move without drawing
 */
@AllArgsConstructor
public class Move implements PathCommand {

    double x;
    double y;
    boolean relative;

    @Override
    public Position3D getNextPos(Position2D last) {
        if (relative)
            return new Position3D(last.getX() + x, last.getY() + y, false);

        return new Position3D(x, y, false);
    }
}
