package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;

import java.util.List;

/**
 * Interface for all path commands (move, line, curves, ...)
 */
public interface PathCommand {
    List<Position3D> getNextPos(Position2D last);
}
