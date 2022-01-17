package de.dofe.ev3;

import de.dofe.ev3.geometry.svg.SVGParser;
import de.dofe.ev3.geometry.svg.path.PathCommand;
import de.dofe.ev3.geometry.svg.path.PathComponent;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import de.dofe.ev3.rest.RestApp;
import de.dofe.ev3.rest.RobotWebSocket;
import de.dofe.ev3.visualizer.Visualizer;
import lejos.hardware.Sound;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static de.dofe.ev3.Paper.*;

public class Main {

    private static final int WEBSOCKET_PORT = 80;

    /**
     * Entry point for the program on the EV3 brick.
     *
     * @param args The command line arguments.
     */
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) {
        String mTestPath = "m 100 1446 l 0 -1345 l 268 0 l 318 952 l 64 199 l 72 -216 l 322 -936 l 239 0 l 0 1345 l -172 0 l 0 -1126 l -391 1126 l -161 0 l -389 -1145 l 0 1146 l -170 0 z";
        String largeMTestPath = "m 500 7230 l 0 -6725 l 1340 0 l 1590 4760 l 320 995 l 360 -1080 l 1610 -4680 l 1195 0 l 0 6725 l -860 0 l 0 -5630 l -1955 5630 l -805 0 l -1945 -5725 l 0 5730 l -850 0 z";
        String negativeMTestPath = "m -947 6062 l 0 -6725 l 1340 0 l 1590 4760 l 320 995 l 360 -1080 l 1610 -4680 l 1195 0 l 0 6725 l -860 0 l 0 -5630 l -1955 5630 l -805 0 l -1945 -5725 l 0 5730 l -850 0 z";
        String cubicBezierTestPath = "m 100 100 c 7 292 322 14 251 352 m 50 50 l 150 300 c 112 -81 -329 -173 381 -138";
        boolean fitToScale = true;

        List<PathComponent> components = null;
        try {
            components = SVGParser.parse(largeMTestPath);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            new RestApp();
        } catch (IOException e) {
            System.out.println("Could not start REST server");
        }

        Robot robot = new Visualizer();

        System.out.println("Starting WebSocket...");
        RobotWebSocket webSocket = new RobotWebSocket(WEBSOCKET_PORT);
        webSocket.start();
        System.out.println("WebSocket started on port " + WEBSOCKET_PORT);

        // Setup status Subscriptions
        System.out.println("Setting up status subscriptions...");
        robot.registerObserver(webSocket);

        // Set scaling
        if (fitToScale)
            robot.setScaling(getAutoScale(components));
        else
            robot.setScaling(new double[]{1, 0, 0});

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

    /**
     * Calculates automatic scaling and offset for the given path.
     *
     * @param components The path components.
     * @return A double array of the form [scale, offsetX, offsetY].
     */
    private static double[] getAutoScale(List<PathComponent> components) {
        // Simulate positions & find max coordinates
        if (components != null) {
            double maxX = 0;
            double maxY = 0;
            double minX = components.get(0).toClass().getNextPos(new Position2D(0, 0)).get(0).getX();
            double minY = components.get(0).toClass().getNextPos(new Position2D(0, 0)).get(0).getY();

            Position2D last = new Position2D(0, 0);
            for (PathComponent c : components) {
                PathCommand cmd = c.toClass();
                if (cmd != null) {
                    List<Position3D> points = cmd.getNextPos(last);
                    if (points.size() > 1) {
                        for (Position3D p : points) {
                            maxX = Math.max(maxX, p.getX());
                            maxY = Math.max(maxY, p.getY());
                            minX = Math.min(minX, p.getX());
                            minY = Math.min(minY, p.getY());
                        }
                        last = points.get(points.size() - 1);
                    } else {
                        Position3D p = points.get(0);
                        last = p;
                        maxX = Math.max(maxX, p.getX());
                        maxY = Math.max(maxY, p.getY());
                        minX = Math.min(minX, p.getX());
                        minY = Math.min(minY, p.getY());
                    }
                }
            }
            if (maxX == 0 || maxY == 0)
                throw new UnsupportedOperationException("No maximum coordinates found.");

            // Scale coordinates to fit on A4 paper
            double offsetX = 0;
            double offsetY = 0;
            double safetyPx = SAFETY_MARGIN_MM * (DPI / MM_PER_INCH);
            if (minX < safetyPx)
                offsetX = safetyPx - minX;
            if (minY < SAFETY_MARGIN_MM * (DPI / MM_PER_INCH))
                offsetY = safetyPx - minY;

            double paperX = (A4_WIDTH_MM - SAFETY_MARGIN_MM) * (DPI / MM_PER_INCH);
            double paperY = (A4_HEIGHT_MM - SAFETY_MARGIN_MM) * (DPI / MM_PER_INCH);
            double scaleX = paperX / (maxX + offsetX);
            double scaleY = paperY / (maxY + offsetY);

            // Return scale
            return new double[]{Math.min(scaleX, scaleY), offsetX, offsetY};
        }

        throw new UnsupportedOperationException("No components found.");
    }

    // TODO Load SVGs from file
    // TODO Advanced geometry (non-cubic curves, transforms, ...)

    // TODO Prepare paper in tray
    // TODO Translate SVG coordinates to paper coordinates

    // TODO Unit tests
}
