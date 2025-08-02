package com.rikkamus.craftersoneclaimvisualizer.geometry;

import org.joml.Vector2i;

enum Direction {

    RIGHT,
    TOP_RIGHT,
    UP,
    TOP_LEFT,
    LEFT,
    BOTTOM_LEFT,
    DOWN,
    BOTTOM_RIGHT;

    public static Direction of(Vector2i vector) {
        return Direction.relative(new Vector2i(0, -1), vector);
    }

    public static Direction relative(Vector2i from, Vector2i to) {
        if (Vector2iUtil.isZero(from) || Vector2iUtil.isZero(to)) throw new IllegalArgumentException("Cannot determine relative direction from/to a zero vector.");

        long dot = Vector2iUtil.dot(from, to);
        long cross = Vector2iUtil.cross(from, to);

        if (dot == 0) {
            return cross > 0 ? RIGHT : LEFT;
        } else if (cross == 0) {
            return dot > 0 ? UP : DOWN;
        } else if (dot > 0) {
            return cross > 0 ? TOP_RIGHT : TOP_LEFT;
        } else {
            return cross > 0 ? BOTTOM_RIGHT : BOTTOM_LEFT;
        }
    }

    public Direction collapseIntoQuadrantClockwise() {
        return switch (this) {
            case RIGHT -> BOTTOM_RIGHT;
            case UP -> TOP_RIGHT;
            case LEFT -> TOP_LEFT;
            case DOWN -> BOTTOM_LEFT;
            default -> this;
        };
    }

}
