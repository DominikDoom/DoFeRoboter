package de.dofe.ev3.positions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Position2D {
    private double x;
    private double y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position2D that = (Position2D) o;
        return Math.abs(that.getX() - getX()) < 1 && Math.abs(that.getY() - getY()) < 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Position2D[" +
                "x=" + x +
                ", y=" + y +
                ']';
    }
}
