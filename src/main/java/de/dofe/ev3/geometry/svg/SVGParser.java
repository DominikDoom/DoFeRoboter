package de.dofe.ev3.geometry.svg;

import de.dofe.ev3.geometry.svg.path.PathCommandType;
import de.dofe.ev3.geometry.svg.path.PathComponent;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides methods to parse SVG files into
 * a format that is usable for our Plott3r implementation.
 */
public class SVGParser {

    private SVGParser() {
        throw new IllegalStateException("Utility class");
    }

    private static final String PARSE_ERROR = "Malformed path (first error at %d)";

    private static final String K_PATH_REGEX = "<path.* d=[\"'](.*?)[\"'].*/?>";
    private static final String K_POLYLINE_REGEX = "<polyline.* points=[\"'](.*?)[\"'].*/?>";
    private static final String K_POLYGON_REGEX = "<polygon.* points=[\"'](.*?)[\"'].*/?>";
    private static final String K_RECT_REGEX = "<rect.* x=[\"'](.*?)[\"'] y=[\"'](.*?)[\"'] width=[\"'](.*?)[\"'] height=[\"'](.*?)[\"'].*/?>";

    private static final String K_COMMAND_TYPE_REGEX = "^[\t\n\f\r ]*([MLHVZCSQTAmlhvzcsqta])[\t\n\f\r ]*";
    private static final String K_FLAG_REGEX = "^[01]";
    private static final String K_NUMBER_REGEX = "^[+-]?(([0-9]*\\.[0-9]+)|([0-9]+\\.)|([0-9]+))([eE][+-]?[0-9]+)?";
    private static final String K_COORDINATE_REGEX = K_NUMBER_REGEX;
    private static final String K_COMMA_WSP = "^(([\t\n\f\r ]+,?[\t\n\f\r ]*)|(,[\t\n\f\r ]*))";

    private static final HashMap<String, String[]> grammar;

    static {
        grammar = new HashMap<>();
        grammar.put("M", new String[]{K_COORDINATE_REGEX, K_COORDINATE_REGEX});
        grammar.put("L", new String[]{K_COORDINATE_REGEX, K_COORDINATE_REGEX});
        grammar.put("H", new String[]{K_COORDINATE_REGEX});
        grammar.put("V", new String[]{K_COORDINATE_REGEX});
        grammar.put("Z", new String[]{});
        grammar.put("C", new String[]{K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX});
        grammar.put("S", new String[]{K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX});
        grammar.put("Q", new String[]{K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX});
        grammar.put("T", new String[]{K_COORDINATE_REGEX, K_COORDINATE_REGEX});
        grammar.put("A", new String[]{K_NUMBER_REGEX, K_NUMBER_REGEX, K_COORDINATE_REGEX, K_FLAG_REGEX, K_FLAG_REGEX, K_COORDINATE_REGEX, K_COORDINATE_REGEX});
    }

    /**
     * Parses coordinate & argument components for the given path command.
     *
     * @param type   The path command to parse.
     * @param path   The path string.
     * @param cursor The cursor position (to trim the path string up to the current inspected command).
     * @return The parsed path components. The HashMap key is the cursor position, while the values are the
     * correlating components.
     */
    private static LinkedHashMap<Integer, ArrayList<ArrayList<String>>> components(String type, String path, int cursor) throws ParseException {
        final String[] expectedRegexList = grammar.get(type.toUpperCase());

        final ArrayList<ArrayList<String>> components = new ArrayList<>();
        while (cursor <= path.length()) {
            final ArrayList<String> component = new ArrayList<>();
            component.add(type);

            for (String regex : expectedRegexList) {
                Matcher matcher = Pattern.compile(regex).matcher(path.substring(cursor));
                String match = matcher.find() ? matcher.group(0) : null;

                if (match != null) {
                    component.add(match);
                    cursor += match.length();

                    Matcher wsMatcher = Pattern.compile(K_COMMA_WSP).matcher(path.substring(cursor));
                    String ws = wsMatcher.find() ? wsMatcher.group() : null;
                    if (ws != null) {
                        cursor += ws.length();
                    }
                } else if (component.size() == 1) {
                    LinkedHashMap<Integer, ArrayList<ArrayList<String>>> out = new LinkedHashMap<>();
                    out.put(cursor, components);
                    return out;
                } else {
                    throw new ParseException(String.format(PARSE_ERROR, cursor), cursor);
                }
            }

            components.add(component);
            if (expectedRegexList.length == 0) {
                LinkedHashMap<Integer, ArrayList<ArrayList<String>>> out = new LinkedHashMap<>();
                out.put(cursor, components);
                return out;
            }
            if (type.equals("m"))
                type = "l";
            else if (type.equals("M"))
                type = "L";
        }

        throw new ParseException(String.format(PARSE_ERROR, cursor), cursor);
    }

    /**
     * Parses the given svg path into a list of drawable path commands.
     *
     * @param path The path string.
     * @return A list of {@link PathComponent} data objects.
     */
    public static List<PathComponent> parse(String path) throws ParseException {
        int cursor = 0;
        ArrayList<PathComponent> tokens = new ArrayList<>();

        while (cursor < path.length()) {
            Matcher matcher = Pattern.compile(K_COMMAND_TYPE_REGEX).matcher(path.substring(cursor));
            String[] match = matcher.find()
                    ? new String[]{matcher.group(0), matcher.group(1)}
                    : null;

            if (match != null) {
                String command = match[1];
                cursor += match[0].length();

                LinkedHashMap<Integer, ArrayList<ArrayList<String>>> componentList = components(command, path, cursor);
                cursor = componentList.keySet().iterator().next();

                // Components to properly enum-mapped tokens
                for (ArrayList<String> component : componentList.values().iterator().next()) {
                    String type = component.get(0);
                    boolean isZ = type.equalsIgnoreCase("Z");
                    double[] values = isZ ? null : new double[component.size() - 1];
                    Boolean relative = isZ ? null : Character.isLowerCase(type.charAt(0));

                    if (!isZ) {
                        for (int i = 1; i < component.size(); i++) {
                            values[i - 1] = Double.parseDouble(component.get(i));
                        }
                    }
                    tokens.add(new PathComponent(PathCommandType.get(type), values, relative));
                }
            } else {
                throw new ParseException(String.format(PARSE_ERROR, cursor), cursor);
            }
        }

        return tokens;
    }

    /**
     * Extracts path strings from raw svg file contents.
     *
     * @param svg The raw svg string.
     * @return A list of path strings.
     */
    public static List<String> extractPaths(String svg) {
        List<String> paths = new ArrayList<>();
        Matcher matcher = Pattern.compile(K_PATH_REGEX).matcher(svg);
        while (matcher.find()) {
            paths.add(matcher.group(1));
        }
        return paths;
    }

    /**
     * Extracts polylines from raw svg file contents
     * & converts it into a path for further parsing.
     *
     * @param svg The raw svg string.
     * @return A list of path-converted polyline strings.
     */
    public static List<String> extractPolylines(String svg) {
        List<String> paths = new ArrayList<>();
        Matcher matcher = Pattern.compile(K_POLYLINE_REGEX).matcher(svg);
        while (matcher.find()) {
            paths.add("M " + matcher.group(1));
        }
        return paths;
    }

    /**
     * Extracts polygons from raw svg file contents
     * & converts it into a path for further parsing.
     * Same as {@link #extractPolylines(String)}, except
     * that the polygon is closed.
     *
     * @param svg The raw svg string.
     * @return A list of path-converted polygon strings.
     */
    public static List<String> extractPolygons(String svg) {
        List<String> paths = new ArrayList<>();
        Matcher matcher = Pattern.compile(K_POLYGON_REGEX).matcher(svg);
        while (matcher.find()) {
            paths.add("M " + matcher.group(1) + " Z");
        }
        return paths;
    }

    /**
     * Extracts rectangles from raw svg file contents.
     * Since the robot cannot draw filled rectangles,
     * they are also converted into paths by calculating
     * the four corners of the rectangle and
     * treating them as polygon coordinates.
     *
     * @param svg The raw svg string.
     * @return A list of path-converted rectangle strings.
     */
    public static List<String> extractRects(String svg) {
        List<String> paths = new ArrayList<>();
        Matcher matcher = Pattern.compile(K_RECT_REGEX).matcher(svg);
        while (matcher.find()) {
            double x = Double.parseDouble(matcher.group(1));
            double y = Double.parseDouble(matcher.group(2));
            double width = Double.parseDouble(matcher.group(3));
            double height = Double.parseDouble(matcher.group(4));

            paths.add(String.format(Locale.ENGLISH, "M %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f Z", x, y, x + width, y, x + width, y + height, x, y + height));
        }
        return paths;
    }
}
