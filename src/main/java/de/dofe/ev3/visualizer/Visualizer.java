package de.dofe.ev3.visualizer;

import de.dofe.ev3.Robot;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;

import javax.swing.*;
import java.awt.*;

/**
 * This class is a drop-in replacement for Robot.
 * <br>
 * It is used to visualize the robot's movements without needing to connect to the EV3.
 */
public class Visualizer extends Robot {

    private Position3D currentPosition;
    private boolean zActive;

    private static final int A4_WIDTH = 2480;
    private static final int A4_HEIGHT = 3508;
    private static final float VIEW_SCALE_FACTOR = 0.25f;
    private static final float DRAW_SCALE_FACTOR = 0.4f;

    private final GPanel panel;

    /**
     * Creates a new Visualizer and shows the GUI.
     */
    public Visualizer() {
        JFrame frame = new JFrame("Plott3r Visualizer");
        panel = new GPanel();

        panel.setPreferredSize(new Dimension((int) (A4_WIDTH * VIEW_SCALE_FACTOR), (int) (A4_HEIGHT * VIEW_SCALE_FACTOR)));
        frame.setPreferredSize(new Dimension((int) (A4_WIDTH * VIEW_SCALE_FACTOR), (int) (A4_HEIGHT * VIEW_SCALE_FACTOR)));
        frame.getContentPane().add(panel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        panel.repaint();
    }

    @Override
    public void removePaper() {
        // Not needed in visualizer
    }

    @Override
    public void preparePaper() {
        // Not needed in visualizer
    }

    @Override
    public void moveToHomePosition() {
        this.currentPosition = new Position3D(0, 0, false);
    }

    @Override
    public void moveToPosition(Position2D position2D, int mmSec) {
        this.moveToPosition(new Position3D(position2D, zActive), mmSec);
    }

    /**
     * Moves / draws to a new position on the JPanel.
     * <br>
     * Moves are blue, draws are red.
     */
    @Override
    public void moveToPosition(Position3D position, int mmSec) {
        zActive = position.isZ();

        Position2D scaledPosition = new Position2D(position.getX() * DRAW_SCALE_FACTOR, position.getY() * DRAW_SCALE_FACTOR);

        panel.draw(currentPosition, scaledPosition, zActive ? Color.RED : Color.BLUE);

        this.currentPosition = new Position3D(scaledPosition.getX(), scaledPosition.getY(), zActive);
    }

    private void resetTachoCounts() {
        // Not needed in visualizer
    }

    public void stop() {
        // Not needed in visualizer
    }

}
