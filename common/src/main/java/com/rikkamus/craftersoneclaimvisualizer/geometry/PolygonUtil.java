package com.rikkamus.craftersoneclaimvisualizer.geometry;

import lombok.experimental.UtilityClass;
import org.joml.Vector2i;

import java.awt.*;
import java.util.*;
import java.util.List;

@UtilityClass
public class PolygonUtil {

    private static final Map<Direction, List<Direction>> DIRECTION_TO_SOURCE_CORNERS = Map.of(
        Direction.TOP_RIGHT, List.of(Direction.TOP_LEFT, Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT),
        Direction.TOP_LEFT, List.of(Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT),
        Direction.BOTTOM_LEFT, List.of(Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT, Direction.TOP_LEFT),
        Direction.BOTTOM_RIGHT, List.of(Direction.TOP_RIGHT, Direction.TOP_LEFT, Direction.BOTTOM_LEFT)
    );

    private static final Map<Direction, Integer> RELATIVE_DIRECTION_CORNER_COUNTS = Map.of(
        Direction.TOP_RIGHT, 1,
        Direction.TOP_LEFT, 2,
        Direction.BOTTOM_LEFT, 3,
        Direction.BOTTOM_RIGHT, 1
    );

    private static final List<Direction> FULL_BLOCK_CORNERS = List.of(Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT, Direction.TOP_LEFT);

    public static Polygon createPolygonFromBlockPoints(SequencedCollection<Vector2i> blockPoints) {
        blockPoints = withoutZeroLengthEdges(blockPoints);

        if (blockPoints.isEmpty()) throw new IllegalArgumentException("Cannot create polygon without points.");
        if (blockPoints.size() == 1) return createPolygonFromRawPoints(formBlock(blockPoints.getFirst(), FULL_BLOCK_CORNERS));

        blockPoints = counterclockwise(blockPoints);

        SequencedCollection<Vector2i> adjustedPoints = new ArrayList<>();
        PolygonPointIterator<Vector2i> iterator = new PolygonPointIterator<>(blockPoints);

        while (iterator.hasNext()) {
            Vector2i blockPos = iterator.next();

            Vector2i prevHeading = Vector2iUtil.directionTo(iterator.peekPreviousNeighbor(), blockPos);
            Vector2i nextHeading = Vector2iUtil.directionTo(blockPos, iterator.peekNextNeighbor());

            Direction directionToSource = Direction.of(prevHeading.negate(new Vector2i()));
            Direction relativeDirection = Direction.relative(prevHeading, nextHeading);

            adjustedPoints.addAll(formBlockFromDirections(
                blockPos,
                directionToSource.collapseIntoQuadrantClockwise(),
                relativeDirection.collapseIntoQuadrantClockwise()
            ));
        }

        adjustedPoints = withoutUselessPoints(adjustedPoints);

        // This should never happen with the current algorithm
        if (adjustedPoints.size() < 3) throw new IllegalArgumentException("A polygon must have at least three points.");

        return createPolygonFromRawPoints(adjustedPoints);
    }

    public static Polygon createPolygonFromRawPoints(SequencedCollection<Vector2i> points) {
        int[] xpoints = new int[points.size()];
        int[] ypoints = new int[points.size()];

        int i = 0;
        for (Vector2i point : points) {
            xpoints[i] = point.x;
            ypoints[i] = point.y;
            i++;
        }

        return new Polygon(xpoints, ypoints, points.size());
    }

    private static SequencedCollection<Vector2i> withoutZeroLengthEdges(SequencedCollection<Vector2i> points) {
        if (points.size() < 2) return points;

        List<Vector2i> filteredPoints = new LinkedList<>();
        PolygonPointIterator<Vector2i> iterator = new PolygonPointIterator<>(points);

        while (iterator.hasNext()) {
            Vector2i point = iterator.next();

            Vector2i prevNeighbor = filteredPoints.isEmpty() ? points.getLast() : filteredPoints.getLast();
            Vector2i nextNeighbor = iterator.hasNext() ? iterator.peekNextNeighbor() : filteredPoints.isEmpty() ? point : filteredPoints.getFirst();

            if ((filteredPoints.isEmpty() && !iterator.hasNext()) || (!points.equals(prevNeighbor) && !point.equals(nextNeighbor))) filteredPoints.add(point);
        }

        return filteredPoints;
    }

    private static SequencedCollection<Vector2i> withoutUselessPoints(SequencedCollection<Vector2i> points) {
        List<Vector2i> filteredPoints = new LinkedList<>();
        PolygonPointIterator<Vector2i> iterator = new PolygonPointIterator<>(points);

        while (iterator.hasNext()) {
            Vector2i point = iterator.next();

            if (!filteredPoints.isEmpty() || iterator.hasNext()) {
                Vector2i prevNeighbor = filteredPoints.isEmpty() ? points.getLast() : filteredPoints.getLast();
                Vector2i nextNeighbor = iterator.hasNext() ? iterator.peekNextNeighbor() : filteredPoints.isEmpty() ? point : filteredPoints.getFirst();

                // Exclude zero-length edges and points that are collinear with and between their neighbors
                if (Vector2iUtil.isPointOnEdgeBetween(point, prevNeighbor, nextNeighbor, true)) continue;
            }

            filteredPoints.add(point);
        }

        return filteredPoints;
    }

    private static SequencedCollection<Vector2i> counterclockwise(SequencedCollection<Vector2i> points) {
        if (points.size() < 3) return points;

        long sum = 0;
        PolygonPointIterator<Vector2i> iterator = new PolygonPointIterator<>(points);

        while (iterator.hasNext()) {
            Vector2i a = iterator.next();
            Vector2i b = iterator.peekNextNeighbor();
            sum += (((long) b.x) - ((long) a.x)) * (((long) b.y) + ((long) a.y));
        }

        return sum < 0 ? points.reversed() : points;
    }

    private static List<Vector2i> formBlockFromDirections(Vector2i blockPos, Direction directionToSource, Direction relativeDirection) {
        return formBlock(blockPos, DIRECTION_TO_SOURCE_CORNERS.get(directionToSource).subList(0, RELATIVE_DIRECTION_CORNER_COUNTS.get(relativeDirection)));
    }

    private static List<Vector2i> formBlock(Vector2i blockPos, SequencedCollection<Direction> corners) {
        return corners.stream().map(direction -> switch (direction) {
            case TOP_RIGHT -> blockPos.add(1, 0, new Vector2i());
            case TOP_LEFT -> new Vector2i(blockPos);
            case BOTTOM_LEFT -> blockPos.add(0, 1, new Vector2i());
            case BOTTOM_RIGHT -> blockPos.add(1, 1, new Vector2i());
            default -> throw new IllegalArgumentException("Invalid direction.");
        }).toList();
    }

}
