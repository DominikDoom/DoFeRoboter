package de.dofe.ev3.positions;

import lombok.Getter;

import java.util.Objects;

public class Position3D extends Position2D {
    @Getter
    private final boolean z;

    public Position3D(Position2D position2D, boolean z) {
        super(position2D.getX(), position2D.getY());
        this.z = z;
    }

    public Position3D(double x, double y, boolean z) {
        super(x, y);
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position3D)) return false;
        if (!super.equals(o)) return false;
        Position3D that = (Position3D) o;
        return isZ() == that.isZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isZ());
    }

    @Override
    public String toString() {
        return "Position3D[" +
                "x=" + this.getX() +
                "y=" + this.getY() +
                "z=" + z +
                ']';
    }
}
