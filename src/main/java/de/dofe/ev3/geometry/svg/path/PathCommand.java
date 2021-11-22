package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position3D;

/**
 * Interface for all path commands (move, line, curves, ...)
 */
public interface PathCommand {
    Position3D getNextPos();
}
