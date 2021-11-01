package de.dofe.ev3.position;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A simplified substitute for {@link java.awt.Point} since
 * the awt package isn't included in the embedded java compact2
 * profile for the EV3.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Position2D {

    private final double x;
    private final double y;
}
