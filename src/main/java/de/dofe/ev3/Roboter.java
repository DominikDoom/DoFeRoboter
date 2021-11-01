package de.dofe.ev3;

import de.dofe.ev3.achse.DualPositionAchse;
import de.dofe.ev3.achse.MultiPositionAchse;
import de.dofe.ev3.motor.Einbaurichtung;
import de.dofe.ev3.positions.Position2D;
import de.dofe.ev3.positions.Position3D;
import de.dofe.ev3.reifen.Reifen;
import de.dofe.ev3.sensor.LichtSensor;
import de.dofe.ev3.sensor.TouchSensor;
import de.dofe.ev3.zahnrad.Zahnrad;
import de.dofe.ev3.zahnrad.Zahnradsatz;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class Roboter {
    private Position3D currentPosition;

    private final MultiPositionAchse xAchse = new MultiPositionAchse(new TouchSensor(SensorPort.S1), MotorPort.C, Einbaurichtung.UMGEKEHRT, new Reifen(40.0), new Zahnradsatz(Zahnrad.KLEIN, Zahnrad.GROSS));
    private final MultiPositionAchse yAchse = new MultiPositionAchse(new LichtSensor(SensorPort.S2), MotorPort.B, Einbaurichtung.UMGEKEHRT, new Reifen(43.2), new Zahnradsatz(Zahnrad.KLEIN, Zahnrad.GROSS));
    private final DualPositionAchse zAchse = new DualPositionAchse(null, MotorPort.A, Einbaurichtung.REGULAER, null);

    public Roboter() {

    }

    public void entfernePapier() throws InterruptedException {
        zAchse.deaktiviere();
        yAchse.getMotor().getRegulatedMotor().setSpeed(Integer.MAX_VALUE);
        yAchse.backward(2000);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.exit(0);
    }

    public Position3D getCurrentPosition() {
        return this.currentPosition;
    }

    public MultiPositionAchse getXAchse() {
        return this.xAchse;
    }

    public MultiPositionAchse getYAchse() {
        return this.yAchse;
    }

    protected void moveToHomePosition() throws InterruptedException {
        zAchse.deaktiviere();
        xAchse.getMotor().getRegulatedMotor().setSpeed(50);
        while (!xAchse.getSensor().isAktiv()) {
            xAchse.backward();
        }
        xAchse.getMotor().getRegulatedMotor().stop();
        xAchse.forward();
        Delay.msDelay(200);
        xAchse.getMotor().getRegulatedMotor().stop();
        this.currentPosition = new Position3D(0, 0, false);
        this.resetTachoCounts();
    }

    public void moveToPosition(Position2D position2D, int mmSec) throws InterruptedException {
        this.moveToPosition(new Position3D(position2D, this.zAchse.isAktiv()), mmSec);
    }

    public void moveToPosition(Position3D position, int mmSec) throws InterruptedException {
        if (position.isZ())
            this.zAchse.aktiviere();
        else
            this.zAchse.deaktiviere();

        double deltaX = position.getX() - currentPosition.getX();
        double deltaY = position.getY() - currentPosition.getY();
        double hypo = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double time = hypo / mmSec;

        xAchse.getMotor().getRegulatedMotor().synchronizeWith(new RegulatedMotor[]{yAchse.getMotor().getRegulatedMotor()});

        //xAchse.getMotor().getRegulatedMotor().setSpeed((int) (deltaX / time));
        //yAchse.getMotor().getRegulatedMotor().setSpeed((int) (deltaY / time));

        this.xAchse.setSpeedMM(deltaX / time);
        this.xAchse.setSpeedMM(deltaY / time);

        xAchse.getMotor().getRegulatedMotor().startSynchronization();

        xAchse.rotateMm(deltaX);
        yAchse.rotateMm(deltaY);

        xAchse.getMotor().getRegulatedMotor().endSynchronization();

        xAchse.getMotor().getRegulatedMotor().waitComplete();
        yAchse.getMotor().getRegulatedMotor().waitComplete();

        this.currentPosition = new Position3D(xAchse.getPositionFromTachoCount(), yAchse.getPositionFromTachoCount(), zAchse.isAktiv());

    }

    private void resetTachoCounts() {
        this.xAchse.resetTachoCount();
        this.yAchse.resetTachoCount();
        if (xAchse.getTachoCount() != 0 || yAchse.getTachoCount() != 0)
            throw new RuntimeException("Couldn't reset TachoCount");
    }

    public void stop() {
        xAchse.getMotor().getRegulatedMotor().stop();
        yAchse.getMotor().getRegulatedMotor().stop();
        zAchse.getMotor().getRegulatedMotor().stop();
    }

    /* public void zeichneGeometrischeFigur(GeometrischeFigur geo, int mmSec) throws InterruptedException {
        this.zAchse.deaktiviere();
        this.moveToPosition(geo.getPositions().get(0), 1000);
        this.zAchse.aktiviere();
        for (Position2D pos : geo.getPositions()) {
            this.moveToPosition(pos, mmSec);
        }
        this.zAchse.deaktiviere();
    } */
}
