package de.dofe.ev3.visualizer;

import de.dofe.ev3.Robot;
import de.dofe.ev3.position.Position2D;
import de.dofe.ev3.position.Position3D;

import javax.swing.*;
import java.awt.*;

import static de.dofe.ev3.Paper.*;

/**
 * This class is a drop-in replacement for Robot.
 * <br>
 * It is used to visualize the robot's movements without needing to connect to the EV3.
 */
public class Visualizer extends Robot {

    private Position3D currentPosition;
    private boolean zActive;

    private static final boolean SHOW_AGE = true;

    private final GPanel panel;

    /**
     * Creates a new Visualizer and shows the GUI.
     */
    public Visualizer() {
        JFrame frame = new JFrame("Plott3r Visualizer");
        panel = new GPanel();

        double paperPxWidth = A4_WIDTH_MM * (DPI / MM_PER_INCH);
        double paperPxHeight = A4_HEIGHT_MM * (DPI / MM_PER_INCH);

        panel.setPreferredSize(new Dimension((int) (paperPxWidth * VIEW_SCALE_FACTOR), (int) (paperPxHeight * VIEW_SCALE_FACTOR)));
        frame.setPreferredSize(new Dimension((int) (paperPxWidth * VIEW_SCALE_FACTOR), (int) (paperPxHeight * VIEW_SCALE_FACTOR)));
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

        Position2D scaledPosition = new Position2D(
                ((position.getX() * getScaleFactor()) + getOffsetX()) * VIEW_SCALE_FACTOR,
                ((position.getY() * getScaleFactor()) + getOffsetY()) * VIEW_SCALE_FACTOR
        );

        int totalPaths = getTotalPaths();
        int currentPath = getCurrentPathIndex();

        float alpha = SHOW_AGE ? (currentPath / (float) totalPaths) * 255 : 255f;

        Color moveColor = new Color(0, 0, 255, (int) alpha / 4);
        Color drawColor = new Color(255, 0, 0, (int) alpha);

        panel.draw(currentPosition, scaledPosition, zActive ? drawColor : moveColor);

        this.currentPosition = new Position3D(scaledPosition.getX(), scaledPosition.getY(), zActive);
    }

    @Override
    public void stop() {
        // Not needed in visualizer
    }

}
