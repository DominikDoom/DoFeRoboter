package de.dofe.ev3;

import de.dofe.ev3.rest.RestApp;
import de.dofe.ev3.rest.RobotWebSocket;
import de.dofe.ev3.rest.StatsHandler;
import de.dofe.ev3.status.Status;
import de.dofe.ev3.visualizer.Visualizer;
import lejos.hardware.Sound;
import lombok.Getter;

import java.io.IOException;

public class Main {

    private static final int WEBSOCKET_PORT = 80;

    @Getter
    private static Robot robot;

    @Getter
    private static StatsHandler statsHandler;

    /**
     * Entry point for the program on the EV3 brick.
     *
     * @param args The command line arguments.
     */
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) {
        robot = new Robot();

        statsHandler = new StatsHandler();

        try {
            new RestApp();
        } catch (IOException e) {
            System.out.println("Could not start REST server");
        }

        System.out.println("Starting WebSocket...");
        RobotWebSocket webSocket = new RobotWebSocket(WEBSOCKET_PORT);
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
