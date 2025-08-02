package com.rikkamus.craftersoneclaimvisualizer.geometry;

import lombok.experimental.UtilityClass;
import org.joml.Vector2i;

@UtilityClass
public class Vector2iUtil {

    public static long dot(Vector2i a, Vector2i b) {
        return ((long) a.x) * ((long) b.x) + ((long) a.y) * ((long) b.y);
    }

    public static long cross(Vector2i a, Vector2i b) {
        return ((long) a.x) * ((long) b.y) - ((long) a.y) * ((long) b.x);
    }

    public static Vector2i directionTo(Vector2i from, Vector2i to) {
        return to.sub(from, new Vector2i());
    }

    public static boolean isZero(Vector2i vector) {
        return vector.x == 0 && vector.y == 0;
    }

    public static boolean isParallelWith(Vector2i a, Vector2i b) {
        Direction direction = Direction.relative(a, b);
        return direction == Direction.UP || direction == Direction.DOWN;
    }

    public static boolean isPointOnEdgeBetween(Vector2i point, Vector2i a, Vector2i b, boolean inclusive) {
        if (point.equals(a) || point.equals(b)) return inclusive;
        else return Direction.relative(directionTo(a, point), directionTo(point, b)) == Direction.UP;
    }

}
