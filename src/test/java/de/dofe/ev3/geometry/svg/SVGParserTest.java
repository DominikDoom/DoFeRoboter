package de.dofe.ev3.geometry.svg;

import de.dofe.ev3.geometry.svg.path.PathComponent;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.dofe.ev3.geometry.svg.SVGParser.parse;
import static de.dofe.ev3.geometry.svg.path.PathCommandType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SVGParserTest {

    @Test
    void moveTo() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(MOVE_TO, new double[]{10, 20}, true)
        );

        assertThrows(ParseException.class, () -> parse("m 10"));
        assertEquals(parse("m 10 20"), expected);
    }

    @Test
    void exponents() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(MOVE_TO, new double[]{1e3, 2e-3}, true)
        );

        assertEquals(parse("m 1e3 2e-3"), expected);
    }

    @Test
    void noWSBeforeNegativeSign() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(MOVE_TO, new double[]{46, -86}, false)
        );

        assertEquals(parse("M46-86"), expected);
    }

    @Test
    void overloadedMoveTo() throws ParseException {
        List<PathComponent> expected = Arrays.asList(
                new PathComponent(MOVE_TO, new double[]{12.5, 52}, true),
                new PathComponent(LINE_TO, new double[]{39, 0}, true),
                new PathComponent(LINE_TO, new double[]{0, -40}, true),
                new PathComponent(LINE_TO, new double[]{-39, 0}, true),
                new PathComponent(CLOSE_PATH, null, null)
        );

        assertEquals(parse("m 12.5,52 39,0 0,-40 -39,0 z"), expected);
    }

    @Test
    void lineTo() throws ParseException {
        List<PathComponent> expectedA = Collections.singletonList(
                new PathComponent(LINE_TO, new double[]{10, 10}, true)
        );

        List<PathComponent> expectedB = Arrays.asList(
                new PathComponent(LINE_TO, new double[]{10, 10}, true),
                new PathComponent(LINE_TO, new double[]{10, 10}, true)
        );

        assertThrows(ParseException.class, () -> parse("l 10 10 0"));
        assertEquals(parse("l 10,10"), expectedA);
        assertEquals(parse("l10 10 10 10"), expectedB);
    }

    @Test
    void horizontalLineTo() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(HORIZONTAL_LINE_TO, new double[]{10.5}, true)
        );

        assertEquals(parse("h 10.5"), expected);
    }

    @Test
    void verticalLineTo() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(VERTICAL_LINE_TO, new double[]{10.5}, true)
        );

        assertEquals(parse("v 10.5"), expected);
    }

    @Test
    void curveTo() throws ParseException {
        List<PathComponent> a = parse("c 50,0 50,100 100,100 50,0 50,-100 100,-100");
        List<PathComponent> b = parse("c 50,0 50,100 100,100 c 50,0 50,-100 100,-100");

        List<PathComponent> expected = Arrays.asList(
                new PathComponent(CURVE_TO, new double[]{50, 0, 50, 100, 100, 100}, true),
                new PathComponent(CURVE_TO, new double[]{50, 0, 50, -100, 100, -100}, true)
        );

        assertEquals(a, expected);
        assertEquals(a, b);
    }

    @Test
    void smoothCurveTo() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(SMOOTH_CURVE_TO, new double[]{1, 2, 3, 4}, false)
        );

        assertEquals(parse("S 1 2, 3 4"), expected);
    }

    @Test
    void quadraticCurveTo() throws ParseException {
        List<PathComponent> expected = Arrays.asList(
                new PathComponent(MOVE_TO, new double[]{10, 80}, false),
                new PathComponent(QUAD_CURVE_TO, new double[]{95, 10, 180, 80}, false)
        );

        assertEquals(parse("M10 80 Q 95 10 180 80"), expected);
    }

    @Test
    void smoothQuadraticCurveTo() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(SMOOTH_QUAD_CURVE_TO, new double[]{1, -200}, false)
        );

        assertThrows(ParseException.class, () -> parse("t 1 2 3"));
        assertEquals(parse("T 1 -200"), expected);
    }

    @Test
    void arcTo() throws ParseException {
        List<PathComponent> expectedA = Collections.singletonList(
                new PathComponent(ARC_TO, new double[]{30, 50, 0, 0, 1, 162.55, 162.45}, false)
        );

        List<PathComponent> expectedB = Collections.singletonList(
                new PathComponent(ARC_TO, new double[]{60, 60, 0, 0, 1, 100, 100}, false)
        );

        assertEquals(parse("A 30 50 0 0 1 162.55 162.45"), expectedA);
        assertEquals(parse("A 60 60 0 01100 100"), expectedB);
    }

    @Test
    void closePath() throws ParseException {
        List<PathComponent> expected = Collections.singletonList(
                new PathComponent(CLOSE_PATH, null, null)
        );

        assertEquals(parse("z"), expected);
    }
}