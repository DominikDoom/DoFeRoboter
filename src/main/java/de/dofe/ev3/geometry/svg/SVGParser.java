package de.dofe.ev3.geometry.svg;

import de.dofe.ev3.geometry.svg.path.Line;
import de.dofe.ev3.geometry.svg.path.Move;
import de.dofe.ev3.geometry.svg.path.PathCommand;
import de.dofe.ev3.position.Position2D;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for simple SVG data.
 * <p>
 * SVG spec from <a href="https://de.wikipedia.org/wiki/Scalable_Vector_Graphics#Elemente">Wikipedia</a>
 * <br>
 * <b>Path commands:</b>
 * <ul>
 *     <li>M x y - move to x,y
 *     <li>L x y - line to x,y
 *     <li>H x - horizontal line to x
 *     <li>V y - vertical line to y
 *     <li>C x1 y1 x2 y2 x y - cubic bezier curve to x,y with control points x1,y1 and x2,y2
 *     <li>S x2 y2 x y - smooth cubic bezier curve to x,y with control points x2,y2
 *     <li>Q x1 y1 x y - quadratic bezier curve to x,y with control point x1,y1
 *     <li>T x y - smooth quadratic bezier curve to x,y
 *     <li>R x y - catmull rom curve to x,y
 *     <li>A rx ry x-axis-rotation large-arc-flag sweep-flag x y - elliptical arc to x,y
 *     <li>Z - close path
 * </ul>
 *
 * <b>Capital letters -> Absolute coordinates</b><br>
 * <b>Small letters -> Relative coordinates</b>
 */
public class SVGParser {

    private SVGParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Parses an SVG path string into a list of
     * sequentially executable path commands.
     *
     * @param pathData The path data to parse
     * @return A list of path commands
     */
    public static List<PathCommand> parsePath(String pathData) {
        ArrayList<PathCommand> pathCommands = new ArrayList<>();

        // Split path data into separate commands
        Pattern r = Pattern.compile("([MLHVCSQTRA] *-?\\d+\\.?\\d*(?: *,? *-?\\d+\\.?\\d*)*+|Z)", Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(pathData);

        while (m.find()) {
            String command = m.group(1);
            // Get command type
            char commandType = command.charAt(0);
            boolean isRelative = Character.isLowerCase(commandType);
            // Remove command type
            command = command.substring(1);

            switch (commandType) {
                case 'M':
                case 'm':
                    pathCommands.add(parseMove(command, isRelative));
                    break;
                case 'L':
                case 'l':
                    pathCommands.addAll(parsePolyLine(command));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown path command: " + commandType);
            }

            System.out.println(command);
        }

        return pathCommands;
    }

    private static Move parseMove(String command, boolean relative) {
        double x = Double.parseDouble(command.split(",")[0]);
        double y = Double.parseDouble(command.split(",")[1]);

        if (relative) {
            // TODO relative move
            return null;
        } else {
            return new Move(new Position2D(x, y));
        }
    }

    private static ArrayList<Line> parsePolyLine(String command) {
        ArrayList<Line> out = new ArrayList<>();

        String[] segments = command.split(" ");
        for (String segment : segments) {
            double x = Double.parseDouble(segment.split(",")[0]);
            double y = Double.parseDouble(segment.split(",")[1]);

            // TODO add line & get start position from somewhere
        }

        return out;
    }
}
