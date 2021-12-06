package de.dofe.ev3.visualizer;

import de.dofe.ev3.position.Position2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
    }
}