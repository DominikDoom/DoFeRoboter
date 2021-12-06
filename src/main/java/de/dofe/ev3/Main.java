package de.dofe.ev3;

import de.dofe.ev3.geometry.svg.SVGParser;
import de.dofe.ev3.geometry.svg.path.PathCommand;
import de.dofe.ev3.geometry.svg.path.PathComponent;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
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
        List<PathComponent> components = null;
        try {
            components = SVGParser.parse("m100,1446 l0,-1345 l268,0 l318,952 l64,199 l72,-216 l322,-936 l239,0 l0,1345 l-172,0 l0,-1126 l-391,1126 l-161,0 l-389,-1145 l0,1145 l-172,0 z");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Robot robot = new Robot();
        Sound.beep();
        robot.moveToHomePosition();

        if (components != null) {
            Position2D last = new Position2D(0, 0);
            for (PathComponent c : components) {
                PathCommand cmd = c.toClass();
                Position3D pos = cmd.getNextPos(last);
                last = pos;
                robot.moveToPosition(pos, 20);
            }
        }
    }

    // TODO Load SVGs from file
    // TODO Parse simple SVGs
    // TODO Geometry classes (Lines, shapes, ...)
    // TODO Advanced geometry (Curves, filled shapes to lines, transforms, ...)
    // TODO Convert absolute path commands to relative or vice versa

    // TODO Prepare paper in tray
    // TODO Define drawing space coordinate system
    // TODO Translate SVG coordinates to paper coordinates

    // TODO KeepAlive / REST API
    // TODO Simple upload GUI

    // TODO Unit tests

}
