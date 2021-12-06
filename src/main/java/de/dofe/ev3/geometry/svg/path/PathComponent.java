package de.dofe.ev3.geometry.svg.path;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class PathComponent {

    private final PathCommandType type;
    private final double[] args;
    private final Boolean relative;

    public PathCommand toClass() {
        switch (type) {
            case MOVE_TO:
                if (args.length != 2) throw new IllegalArgumentException("Invalid number of arguments for MOVE");
                return new Move(args[0], args[1], relative);
            case LINE_TO:
                if (args.length != 2) throw new IllegalArgumentException("Invalid number of arguments for LINE");
                return new Line(args[0], args[1], relative);
            case CURVE_TO:
                //return new Curve();
            case SMOOTH_CURVE_TO:
                //return new SmoothCurve();
            case QUAD_CURVE_TO:
                //return new QuadCurve();
            case SMOOTH_QUAD_CURVE_TO:
                //return new SmoothQuadCurve();
            case ARC_TO:
                //return new Arc();
            case HORIZONTAL_LINE_TO:
                //return new HorizontalLine();
            case VERTICAL_LINE_TO:
                //return new VerticalLine();
            case CLOSE_PATH:
                //return new ClosePath();
            default:
                throw new IllegalArgumentException("Unknown PathCommandType: " + this);
        }
    }
}
