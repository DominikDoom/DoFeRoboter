package de.dofe.ev3;

import de.dofe.ev3.position.Position2D;
import lejos.hardware.Sound;

public class Main {

    /**
     * Entry point for the program on the EV3 brick.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Robot robot = new Robot();
        Sound.beep();
        robot.moveToHomePosition();

        robot.moveToPosition(new Position2D(60, 60), 20);
    }
}
