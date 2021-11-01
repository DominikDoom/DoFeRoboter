package de.dofe.ev3.factory;

import de.dofe.ev3.axis.Axis;
import de.dofe.ev3.axis.DualPositionAxis;
import de.dofe.ev3.axis.MultiPositionAxis;
import de.dofe.ev3.motor.Motor;
import de.dofe.ev3.motor.MountDirection;
import de.dofe.ev3.sensor.LightSensor;
import de.dofe.ev3.sensor.Sensor;
import de.dofe.ev3.sensor.TouchSensor;
import de.dofe.ev3.transmission.TransmissionSet;
import de.dofe.ev3.transmission.unit.Gear;
import de.dofe.ev3.transmission.unit.Wheel;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

/**
 * A singleton factory to construct a representation of the
 * <a href="http://www.jander.me.uk/LEGO/plott3r.html" target="_top">Plott3r robot</a>
 * for LEGO® Mindstorms EV3.
 */
public class RobotFactory {

    /**
     * Thread-safe instance provider for singleton functionality.
     * */
    private static class InstanceHolder {
        private static final RobotFactory instance = new RobotFactory();
    }

    /**
     * The used axes for movement.
     * <ul>
     *     <li>X
     *     <li>Y
     *     <li>Z
     * </ul>
     * */
    public enum Axes {
        X, Y, Z
    }

    private final MultiPositionAxis xAxis;
    private final MultiPositionAxis yAxis;
    private final DualPositionAxis zAxis;

    /**
     * @return The current singleton instance. If none is set, a new one will be created.
     * */
    public static RobotFactory getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Initializes all axes with their proper
     * {@link Sensor}, {@link Motor}, Drive {@link Wheel} and {@link TransmissionSet}
     * configurations.
     * */
    private RobotFactory() {
        xAxis = MultiPositionAxis.builder()
                .sensor(new TouchSensor(SensorPort.S1))
                .motor(new Motor(MotorPort.C, MountDirection.REVERSE))
                .driveUnit(new Wheel(40.0))
                .transmissionUnits(new TransmissionSet(Gear.SMALL, Gear.LARGE))
                .build();

        yAxis = MultiPositionAxis.builder()
                .sensor(new LightSensor(SensorPort.S2))
                .motor(new Motor(MotorPort.B, MountDirection.REVERSE))
                .driveUnit(new Wheel(43.2))
                .transmissionUnits(new TransmissionSet(Gear.SMALL, Gear.LARGE))
                .build();

        zAxis = DualPositionAxis.builder()
                .motor(new Motor(MotorPort.A, MountDirection.REGULAR))
                .build();
    }

    /**
     * Gets the factory-initialized axes.
     *
     * @param a The axis to return (X, Y or Z).
     * @return The corresponding axis.
     */
    public Axis getAxis(Axes a) {
        switch (a) {
            case X:
                return xAxis;
            case Y:
                return yAxis;
            case Z:
                return zAxis;
            default:
                return null;
        }
    }
}
