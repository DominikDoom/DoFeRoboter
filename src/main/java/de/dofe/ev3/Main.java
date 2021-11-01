package de.dofe.ev3;

import de.dofe.ev3.positions.Position2D;
import lejos.hardware.Sound;

public class Main {
    public static void main(String[] args) {
        /*LCD.clear();
        LCD.drawString("First EV3 Program", 0, 5);
        Button.waitForAnyPress();
        LCD.clear();
        LCD.refresh(); */

        try {
            Roboter roboter = new Roboter();
            Sound.beep();
            roboter.moveToHomePosition();

            //Delay.msDelay(1000);
            //roboter.entfernePapier();
            //roboter.moveToHomePosition();
            //Sound.twoBeeps();

            roboter.moveToPosition(new Position2D(60,60), 20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
