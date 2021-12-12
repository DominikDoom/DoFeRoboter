package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * A move without drawing
 */
@AllArgsConstructor
public class Move implements PathCommand {

    double x;
    double y;
    boolean relative;

    @Override
    public List<Position3D> getNextPos(Position2D last) {
        if (relative)
            return Collections.singletonList(new Position3D(last.getX() + x, last.getY() + y, false));

        return Collections.singletonList(new Position3D(x, y, false));
    }
}
