package de.dofe.ev3;

/**
 * A collection of constants to define A4 paper sizes.
 * <br>
 * Used in most calculations to scale SVG coordinates.
 */
public final class Paper {

    private Paper() {
        throw new IllegalStateException("Utility class");
    }

    public static final int A4_WIDTH_MM = 210;
    public static final int A4_HEIGHT_MM = 297;
    public static final int DPI = 300;
    public static final float MM_PER_INCH = 25.4f;
    public static final int SAFETY_MARGIN_MM = 20;
}
