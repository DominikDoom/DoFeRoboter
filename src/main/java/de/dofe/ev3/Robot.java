package de.dofe.ev3;

import de.dofe.ev3.axis.DualPositionAxis;
import de.dofe.ev3.axis.MultiPositionAxis;
import de.dofe.ev3.factory.RobotFactory;
import de.dofe.ev3.geometry.svg.SVGParser;
import de.dofe.ev3.geometry.svg.path.PathCommand;
import de.dofe.ev3.geometry.svg.path.PathCommandType;
import de.dofe.ev3.geometry.svg.path.PathComponent;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import de.dofe.ev3.status.Status;
import de.dofe.ev3.status.Subject;
import de.dofe.ev3.visualizer.Visualizer;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static de.dofe.ev3.Paper.*;
import static de.dofe.ev3.factory.RobotFactory.Axes;

/**
 * This class initializes the robot and provides methods to control it.
 * <p>
 * The structure of the robot is defined in the RobotFactory.
 */
public class Robot extends Subject implements SvgPrinter {

    /**
     * The current position of the tower in relation to the belt & paper
     */
    @Getter
    private Position3D currentPosition;

    @Getter
    private final MultiPositionAxis xAxis;
    @Getter
    private final MultiPositionAxis yAxis;
    @Getter
    private final DualPositionAxis zAxis;

    @Getter
    private double scaleFactor = 1;
    @Getter
    private double offsetX = 0;
    @Getter
    private double offsetY = 0;

    /**
     * The current status of the robot
     */
    @Getter
    private Status status = Status.READY;

    /**
     * Statistics about the robot
     */
    @Getter
    @Setter
    private int pathsParsed = 0;

    // Progress statistics for visualizer opacity
    @Getter
    private int totalPaths;
    @Getter
    private int currentPathIndex;


    /**
     * Initializes the Plott3r robot using the {@link RobotFactory}.
     */
    public Robot() {
        if (this instanceof Visualizer) {
            xAxis = null;
            yAxis = null;
            zAxis = null;
            return;
        }

        RobotFactory factory = RobotFactory.getInstance();
        xAxis = (MultiPositionAxis) factory.getAxis(Axes.X);
        yAxis = (MultiPositionAxis) factory.getAxis(Axes.Y);
        zAxis = (DualPositionAxis) factory.getAxis(Axes.Z);
    }

    /**
     * Removes the paper from the tray.
     */
    public void removePaper() {
        zAxis.deactivate();
        yAxis.getMotor().setSpeed(Integer.MAX_VALUE);
        yAxis.backward(2000);
    }

    /**
     * Sets the scaling and offset of the path.
     * @param scaling the component array
     */
    public void setScaling(double[] scaling) {
        scaleFactor = scaling[0];
        offsetX = scaling[1];
        offsetY = scaling[2];
    }

    /**
     * Resets the x position using the touch sensor to detect the bound.
     * <p>
     * Initializes {@link #currentPosition} to [0 , 0, false] afterwards.
     */
    protected void moveToHomePosition() {
        zAxis.deactivate();
        resetXAxis();
        resetYAxis();
        this.currentPosition = new Position3D(0, 0, false);
        this.resetTachoCounts();
    }

    /**
     * Resets the x position using the touch sensor to detect the bound.
     */
    private void resetXAxis() {
        xAxis.getMotor().setSpeed(50);
        while (!xAxis.getSensor().isActive()) {
            xAxis.getMotor().backward();
        }
        xAxis.getMotor().stop();
        xAxis.getMotor().forward();
        Delay.msDelay(200);
        xAxis.getMotor().stop();
    }

    /**
     * Resets the y position using the light sensor to detect the bound.
     */
    private void resetYAxis() {
        yAxis.getMotor().setSpeed(50);

        if (yAxis.getSensor().isActive()) {
            while (yAxis.getSensor().isActive()) {
                yAxis.getMotor().backward();
            }
        }

        while (!yAxis.getSensor().isActive()) {
            yAxis.getMotor().forward();
        }

        yAxis.getMotor().stop();
    }

    /**
     * Moves to a specified {@link Position2D}.
     *
     * @param position2D The [x, y] position to move to.
     * @param mmSec      The movement speed in mm/s.
     */
    public void moveToPosition(Position2D position2D, int mmSec) {
        this.moveToPosition(new Position3D(position2D, this.zAxis.isActive()), mmSec);
    }

    /**
     * Moves to a specified {@link Position3D}.
     * If z is active, lower it before moving to the coordinates.
     *
     * @param position The [x, y] position to move to.
     * @param mmSec    The movement speed in mm/s.
     */
    public void moveToPosition(Position3D position, int mmSec) {
        // Pixel to mm conversion
        float dpiFactor = MM_PER_INCH / DPI; // mm/inch / dpi
        Position3D scaledPosition = new Position3D(
                ((position.getX() * scaleFactor) + offsetX) * dpiFactor,
                ((position.getY() * scaleFactor) + offsetY) * dpiFactor,
                position.isZ());

        if (position.isZ())
            this.zAxis.activate();
        else
            this.zAxis.deactivate();

        double deltaX = scaledPosition.getX() - currentPosition.getX();
        double deltaY = scaledPosition.getY() - currentPosition.getY();
        double hypo = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double time = hypo / mmSec;

        xAxis.getMotor().synchronizeWith(new RegulatedMotor[]{yAxis.getMotor()});

        this.xAxis.setSpeed(deltaX / time);
        this.yAxis.setSpeed(deltaY / time);

        xAxis.getMotor().startSynchronization();

        xAxis.rotateMm(deltaX);
        yAxis.rotateMm(deltaY);

        xAxis.getMotor().endSynchronization();

        xAxis.getMotor().waitComplete();
        yAxis.getMotor().waitComplete();

        this.currentPosition = new Position3D(xAxis.getPositionFromTachoCount(), yAxis.getPositionFromTachoCount(), zAxis.isActive());
    }

    /**
     * Resets the tacho count (degrees of rotation since start)
     * for all axes.
     */
    private void resetTachoCounts() {
        this.xAxis.getMotor().resetTachoCount();
        this.yAxis.getMotor().resetTachoCount();
        if (xAxis.getMotor().getTachoCount() != 0 || yAxis.getMotor().getTachoCount() != 0)
            throw new IllegalStateException("Couldn't reset TachoCount");
    }

    /**
     * Stops all motors.
     */
    public void stop() {
        xAxis.getMotor().stop();
        yAxis.getMotor().stop();
        zAxis.getMotor().stop();
    }

    public void setStatus(Status status) {
        this.status = status;
        notifyObservers(this.status);
    }

    /**
     * Prints all supported geometry in the given svg code.
     * <br>
     * Supported tags are:
     * <ul>
     *     <li>&lt;path&gt;</li>
     *     <li>&lt;rect&gt; (Only in the order x y width height)</li>
     *     <li>&lt;polyline&gt;</li>
     *     <li>&lt;polygon&gt;</li>
     * </ul>
     *
     * @param svg The svg string to be printed.
     */
    @Override
    public void print(String svg) {
        // Extract data from supported tags
        List<String> paths = SVGParser.extractPaths(svg);
        paths.addAll(SVGParser.extractPolylines(svg));
        paths.addAll(SVGParser.extractPolygons(svg));
        paths.addAll(SVGParser.extractRects(svg));

        double[] bounds = new double[4]; // minX, minY, maxX, maxY
        ArrayList<List<PathComponent>> parseCache = new ArrayList<>();
        totalPaths = paths.size();

        for (String path : paths) {
            // Parse path
            List<PathComponent> components = null;
            try {
                components = SVGParser.parse(path);
                parseCache.add(components);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Calculate local bounds
            double[] localBounds = getLocalBounds(components);
            // Update global bounds
            bounds[0] = Math.min(bounds[0], localBounds[0]);
            bounds[1] = Math.min(bounds[1], localBounds[1]);
            bounds[2] = Math.max(bounds[2], localBounds[2]);
            bounds[3] = Math.max(bounds[3], localBounds[3]);
        }

        // Order by x & y offset from 0,0
        parseCache.sort((o1, o2) -> {
            double x1 = o1.get(0).getArgs()[0];
            double y1 = o1.get(0).getArgs()[1];
            double x2 = o2.get(0).getArgs()[0];
            double y2 = o2.get(0).getArgs()[1];

            double distance1 = Math.sqrt(x1 * x1 + y1 * y1);
            double distance2 = Math.sqrt(x2 * x2 + y2 * y2);

            return Double.compare(distance1, distance2);
        });

        // Set scale factor
        double[] scale = getAutoScale(bounds);
        setScaling(scale);

        // Set statistics
        setPathsParsed(paths.size());

        // Draw parsed paths
        for (List<PathComponent> components : parseCache) {
            moveToHomePosition();
            currentPathIndex++;

            Position2D first = null;
            Position2D last = new Position2D(0, 0);

            for (PathComponent c : components) {
                if (c.getType() == PathCommandType.CLOSE_PATH) {
                    if (first == null)
                        throw new IllegalStateException("Path closed without starting point");

                    moveToPosition(new Position3D(first, true), 20);
                    first = null;
                }

                PathCommand cmd = c.toClass();
                if (cmd == null)
                    continue;

                List<Position3D> points = cmd.getNextPos(last);
                if (first == null) first = points.get(0);

                if (points.size() > 1) {
                    for (Position3D p : points) {
                        moveToPosition(p, 20);
                    }
                    last = points.get(points.size() - 1);
                } else {
                    Position3D p = points.get(0);
                    last = p;
                    moveToPosition(p, 20);
                }
            }
        }

        // Reset to home position after drawing
        moveToHomePosition();
    }

    /**
     * Calculates the scale factor and offset for the given bounds.
     *
     * @param bounds the global bounds for a collection of paths.
     * @return A double array of the form [scale, offsetX, offsetY].
     */
    private static double[] getAutoScale(double[] bounds) {
        // Scale coordinates to fit on A4 paper
        double offsetX = 0;
        double offsetY = 0;
        double safetyPx = SAFETY_MARGIN_MM * (DPI / MM_PER_INCH);

        if (bounds[0] < safetyPx)
            offsetX = safetyPx - bounds[0];
        if (bounds[1] < safetyPx)
            offsetY = safetyPx - bounds[1];

        double paperX = (A4_WIDTH_MM) * (DPI / MM_PER_INCH);
        double paperY = (A4_HEIGHT_MM) * (DPI / MM_PER_INCH);
        double scaleX = (paperX - safetyPx * 3) / (bounds[2]);
        double scaleY = (paperY - safetyPx * 3) / (bounds[3]);

        // Return scale
        return new double[]{Math.min(scaleX, scaleY), offsetX, offsetY};
    }

    /**
     * Calculates the bounding box of a single path.
     *
     * @param components The path components.
     * @return A double array of the form [minX, minY, maxX, maxY].
     */
    private static double[] getLocalBounds(List<PathComponent> components) {
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

            // Return bounds
            return new double[]{minX, minY, maxX, maxY};
        }

        throw new UnsupportedOperationException("No components found.");
    }
}
