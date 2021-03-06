package de.dofe.ev3.visualizer;

import de.dofe.ev3.position.Position2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static de.dofe.ev3.Paper.*;

public class GPanel extends JPanel {
    private transient BufferedImage buffer;

    @Override
    public void invalidate() {
        BufferedImage img = new BufferedImage(
                Math.max(1, getWidth()),
                Math.max(1, getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int safetyX = (int) ((int) (SAFETY_MARGIN_MM * (DPI / MM_PER_INCH)) * VIEW_SCALE_FACTOR);
        int safetyY = (int) ((int) (SAFETY_MARGIN_MM * (DPI / MM_PER_INCH)) * VIEW_SCALE_FACTOR);

        System.out.println("Safety margin panel: " + safetyX + "x" + safetyY);

        // Draw safe area
        g2d.setColor(new Color(53, 255, 53, 255));
        g2d.fillRect(safetyX, safetyY, getWidth() - safetyX * 2, getHeight() - safetyY * 2);
        g2d.setColor(getBackground());
        g2d.fillRect(safetyX + 2, safetyY + 2, getWidth() - safetyX * 2 - 4, getHeight() - safetyY * 2 - 4);

        if (buffer != null) {
            g2d.drawImage(buffer, 0, 0, this);
        }
        g2d.dispose();
        buffer = img;
        super.invalidate();
    }

    protected BufferedImage getBuffer() {
        if (buffer == null) {
            buffer = new BufferedImage(
                    Math.max(1, getWidth()),
                    Math.max(1, getHeight()), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = buffer.createGraphics();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
        return buffer;
    }

    public void draw(Position2D start, Position2D end, Color color) {
        BufferedImage bi = getBuffer();
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(
                (int) Math.round(start.getX()), (int) Math.round(start.getY()),
                (int) Math.round(end.getX()), (int) Math.round(end.getY())
        );
        g2d.dispose();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(getBuffer(), 0, 0, this);

        // Draw legend
        g.setColor(new Color(0, 0, 0, 204));
        g.fillRect(0, getHeight() - 20, getWidth(), 20);
        g.setColor(new Color(255, 255, 255, 204));
        g.drawString("Move         Draw", 20, getHeight() - 5);
        g.setColor(Color.BLUE);
        g.fillRect(5, getHeight() - 15, 10, 10);
        g.setColor(Color.RED);
        g.fillRect(60, getHeight() - 15, 10, 10);
    }
}