package de.dofe.ev3.geometry.svg.path;

import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CubicBezier implements PathCommand {

    public static final double SEGMENTS = 20;

    // Start control point
    double c1x;
    double c1y;
    // End control point
    double c2x;
    double c2y;
    // End point
    double endX;
    double endY;

    boolean relative;

    // from https://stackoverflow.com/questions/26481342/how-to-calculate-quadratic-bezier-curve
    private List<Position3D> getCurvePoints(Position2D origin) {
        Position3D ctrl1;
        Position3D ctrl2;
        Position3D end;
        if (relative) {
            ctrl1 = new Position3D(origin.getX() + c1x, origin.getY() + c1y, true);
            ctrl2 = new Position3D(origin.getX() + c2x, origin.getY() + c2y, true);
            end = new Position3D(origin.getX() + endX, origin.getY() + endY, true);
        } else {
            ctrl1 = new Position3D(c1x, c1y, true);
            ctrl2 = new Position3D(c2x, c2y, true);
            end = new Position3D(endX, endY, true);
        }

        ArrayList<Position3D> pointsForReturn = new ArrayList<>();

        float t = 0;
        for (int i = 0; i < SEGMENTS; i++) {
            double x = Math.pow(1 - t, 3) * origin.getX() + 3.0f * Math.pow(1 - t, 2) * t * ctrl1.getX() + 3.0f * (1 - t) * t * t * ctrl2.getX() + t * t * t * end.getX();
            double y = Math.pow(1 - t, 3) * origin.getY() + 3.0f * Math.pow(1 - t, 2) * t * ctrl1.getY() + 3.0f * (1 - t) * t * t * ctrl2.getY() + t * t * t * end.getY();

            t += 1.0f / SEGMENTS;
            Position3D p = new Position3D(x, y, true);
            pointsForReturn.add(p);
        }
        pointsForReturn.add(end);
        return pointsForReturn;
    }


    @Override
    public List<Position3D> getNextPos(Position2D last) {
        return getCurvePoints(last);
    }
}
