package de.dofe.ev3;

import de.dofe.ev3.axis.DualPositionAxis;
import de.dofe.ev3.axis.MultiPositionAxis;
import de.dofe.ev3.factory.RobotFactory;
import de.dofe.ev3.geometry.svg.SVGParser;
import de.dofe.ev3.geometry.svg.path.PathCommand;
import de.dofe.ev3.geometry.svg.path.PathComponent;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import de.dofe.ev3.status.Status;
import de.dofe.ev3.status.Subject;
import de.dofe.ev3.visualizer.Visualizer;
import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import lombok.Getter;

import java.text.ParseException;
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

    private double scaleFactor = 1;
    private double offsetX = 0;
    private double offsetY = 0;

    @Getter
    private Status status = Status.READY;

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
                (position.getX() + offsetX) * dpiFactor * scaleFactor,
                (position.getY() + offsetY) * dpiFactor * scaleFactor,
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

    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setStatus(Status status) {
        this.status = status;
        notifyObservers(this.status);
    }

    @Override
    public void print(String svg) {
        List<String> paths = SVGParser.extractPaths(svg);

        double[] minScale = new double[]{Double.MAX_VALUE, 0, 0};
        for (String path : paths) {
            List<PathComponent> components = null;
            try {
                components = SVGParser.parse(path);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            double[] scale = getAutoScale(components);
            minScale = compareScale(minScale, scale);
        }

        setScaling(minScale);

        for (String path : paths) {
            moveToHomePosition();

            List<PathComponent> components = null;
            try {
                components = SVGParser.parse(path);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (components != null) {
                Position2D last = new Position2D(0, 0);

                for (PathComponent c : components) {
                    PathCommand cmd = c.toClass();
                    if (cmd != null) {
                        List<Position3D> points = cmd.getNextPos(last);
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
            }
        }

        moveToHomePosition();
        Sound.beep();
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

    private double[] compareScale(double[] scale, double[] scale2) {
        return scale2[0] < scale[0] ? scale2 : scale;
    }
}
