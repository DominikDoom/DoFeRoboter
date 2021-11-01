package de.dofe.ev3;

import de.dofe.ev3.axis.DualPositionAxis;
import de.dofe.ev3.axis.MultiPositionAxis;
import de.dofe.ev3.factory.RobotFactory;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import lombok.Getter;

import static de.dofe.ev3.factory.RobotFactory.Axes;

public class Roboter {

    @Getter
    private Position3D currentPosition;

    private final MultiPositionAxis xAxis;
    private final MultiPositionAxis yAxis;
    private final DualPositionAxis zAxis;

    public Roboter() {
        RobotFactory factory = RobotFactory.getInstance();
        xAxis = (MultiPositionAxis) factory.getAxis(Axes.X);
        yAxis = (MultiPositionAxis) factory.getAxis(Axes.Y);
        zAxis = (DualPositionAxis) factory.getAxis(Axes.Z);
    }

    public void removePaper() {
        zAxis.deactivate();
        yAxis.getMotor().setSpeed(Integer.MAX_VALUE);
        yAxis.backward(2000);
    }

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

    public void moveToPosition(Position2D position2D, int mmSec) {
        this.moveToPosition(new Position3D(position2D, this.zAxis.isActive()), mmSec);
    }

    public void moveToPosition(Position3D position, int mmSec) {
        if (position.isZ())
            this.zAxis.activate();
        else
            this.zAxis.deactivate();

        double deltaX = position.getX() - currentPosition.getX();
        double deltaY = position.getY() - currentPosition.getY();
        double hypo = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double time = hypo / mmSec;

        xAxis.getMotor().synchronizeWith(new RegulatedMotor[]{yAxis.getMotor()});

        this.xAxis.setSpeed(deltaX / time);
        this.xAxis.setSpeed(deltaY / time);

        xAxis.getMotor().startSynchronization();

        xAxis.rotateMm(deltaX);
        yAxis.rotateMm(deltaY);

        xAxis.getMotor().endSynchronization();

        xAxis.getMotor().waitComplete();
        yAxis.getMotor().waitComplete();

        this.currentPosition = new Position3D(xAxis.getPositionFromTachoCount(), yAxis.getPositionFromTachoCount(), zAxis.isActive());
    }

    private void resetTachoCounts() {
        this.xAxis.getMotor().resetTachoCount();
        this.yAxis.getMotor().resetTachoCount();
        if (xAxis.getMotor().getTachoCount() != 0 || yAxis.getMotor().getTachoCount() != 0)
            throw new IllegalStateException("Couldn't reset TachoCount");
    }

    public void stop() {
        xAxis.getMotor().stop();
        yAxis.getMotor().stop();
        zAxis.getMotor().stop();
    }
}
