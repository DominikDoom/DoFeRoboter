package de.dofe.ev3;

import de.dofe.ev3.geometry.svg.SVGParser;
import de.dofe.ev3.geometry.svg.path.PathCommand;
import de.dofe.ev3.geometry.svg.path.PathComponent;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import de.dofe.ev3.visualizer.Visualizer;
import lejos.hardware.Sound;

import java.text.ParseException;
import java.util.List;

public class Main {

    /**
     * Entry point for the program on the EV3 brick.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        String mTestPath = "m 100 1446 l 0 -1345 l 268 0 l 318 952 l 64 199 l 72 -216 l 322 -936 l 239 0 l 0 1345 l -172 0 l 0 -1126 l -391 1126 l -161 0 l -389 -1145 l 0 1146 l -170 0 z";
        String cubicBezierTestPath = "m 100 100 c 7 292 322 14 251 352 m 50 50 l 150 300 c 112 -81 -329 -173 381 -138";

        List<PathComponent> components = null;
        try {
            components = SVGParser.parse(cubicBezierTestPath);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Robot robot = new Visualizer();

        //noinspection ConstantConditions
        if (!(robot instanceof Visualizer))
            Sound.beep();

        robot.moveToHomePosition();

        if (components != null) {
            Position2D last = new Position2D(0, 0);
            for (PathComponent c : components) {
                PathCommand cmd = c.toClass();
                if (cmd != null) {
                    List<Position3D> points = cmd.getNextPos(last);
                    if (points.size() > 1) {
                        for (Position3D p : points) {
                            robot.moveToPosition(p, 20);
                        }
                        last = points.get(points.size() - 1);
                    } else {
                        Position3D p = points.get(0);
                        last = p;
                        robot.moveToPosition(p, 20);
                    }
                }
            }
        }
    }

    // TODO Load SVGs from file
    // TODO Geometry classes (Lines, shapes, ...) - WIP
    // TODO Advanced geometry (Curves, filled shapes to lines, transforms, ...)

    // TODO Prepare paper in tray
    // TODO Define drawing space coordinate system
    // TODO Translate SVG coordinates to paper coordinates

    // TODO Unit tests
}
