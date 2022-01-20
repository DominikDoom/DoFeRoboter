package de.dofe.ev3;

import de.dofe.ev3.rest.RestApp;
import de.dofe.ev3.rest.RobotWebSocket;
import de.dofe.ev3.status.Status;
import de.dofe.ev3.visualizer.Visualizer;
import lejos.hardware.Sound;

import java.io.IOException;

public class Main {

    private static final int WEBSOCKET_PORT = 80;
    public static RobotWebSocket webSocket = null;

    /**
     * Entry point for the program on the EV3 brick.
     *
     * @param args The command line arguments.
     */
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) {
        Robot robot = new Robot();

        try {
            new RestApp(robot);
        } catch (IOException e) {
            System.out.println("Could not start REST server");
        }

        System.out.println("Starting WebSocket...");
        webSocket = new RobotWebSocket(WEBSOCKET_PORT);
        webSocket.start();
        System.out.println("WebSocket started on port " + WEBSOCKET_PORT);

        // Setup status Subscriptions
        System.out.println("Setting up status subscriptions...");
        robot.registerObserver(webSocket);
        robot.setStatus(Status.READY);

        if (!(robot instanceof Visualizer))
            Sound.beep();

        robot.moveToHomePosition();
    }

    // TODO Advanced geometry (non-cubic curves, transforms, ...)
}
