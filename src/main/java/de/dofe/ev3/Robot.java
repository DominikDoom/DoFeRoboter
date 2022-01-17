package de.dofe.ev3;

import de.dofe.ev3.axis.DualPositionAxis;
import de.dofe.ev3.axis.MultiPositionAxis;
import de.dofe.ev3.factory.RobotFactory;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import de.dofe.ev3.status.Status;
import de.dofe.ev3.status.Subject;
import de.dofe.ev3.visualizer.Visualizer;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import lombok.Getter;

import static de.dofe.ev3.Paper.DPI;
import static de.dofe.ev3.Paper.MM_PER_INCH;
import static de.dofe.ev3.factory.RobotFactory.Axes;

/**
 * This class initializes the robot and provides methods to control it.
 * <p>
 * The structure of the robot is defined in the RobotFactory.
 */
public class Robot extends Subject {

    /**
     * The current position of the tower in relation to the belt & paper
     */
    @Getter
    private Position3D currentPosition;

    private final MultiPositionAxis xAxis;
    private final MultiPositionAxis yAxis;
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
     * Moves the paper to the base position in the tray.
     */
    public void preparePaper() {
        if (yAxis.getSensor().isActive()) {
            while (yAxis.getSensor().isActive()) {
                yAxis.getMotor().backward();
            }
            yAxis.getMotor().stop();
        } else {
            Delay.msDelay(500);
            preparePaper();
        }
    }

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
        xAxis.getMotor().setSpeed(50);
        while (!xAxis.getSensor().isActive()) {
            xAxis.getMotor().backward();
        }
        xAxis.getMotor().stop();
        xAxis.getMotor().forward();
        Delay.msDelay(200);
        xAxis.getMotor().stop();
        this.currentPosition = new Position3D(0, 0, false);
        this.resetTachoCounts();
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
}
