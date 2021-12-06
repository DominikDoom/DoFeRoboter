package de.dofe.ev3.geometry.svg.path;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PathCommandType {
    MOVE_TO("M"), // Move
    LINE_TO("L"), // Line
    CURVE_TO("C"), // Cubic Bezier Curve
    SMOOTH_CURVE_TO("S"), // Smooth Cubic Bezier Curve
    QUAD_CURVE_TO("Q"), // Quadratic Bezier Curve
    SMOOTH_QUAD_CURVE_TO("T"), // Smooth Quadratic Bezier Curve
    ARC_TO("A"), // Elliptical Arc
    HORIZONTAL_LINE_TO("H"), // Horizontal Line
    VERTICAL_LINE_TO("V"), // Vertical Line
    CLOSE_PATH("Z"); // Close Path

    private final String shortType;
    private static final Map<String, PathCommandType> map;

    static {
        Map<String, PathCommandType> temp = Stream.of(PathCommandType.values())
                .collect(Collectors.toMap(PathCommandType::getShortType, Function.identity()));
        map = Collections.unmodifiableMap(temp);
    }

    PathCommandType(String shortType) {
        this.shortType = shortType;
    }

    public String getShortType() {
        return shortType;
    }

    public static PathCommandType get(String shortType) {
        return map.get(shortType.toUpperCase());
    }
}
