package com.rikkamus.craftersoneclaimvisualizer;

import com.rikkamus.craftersoneclaimvisualizer.geometry.PolygonUtil;
import org.joml.Vector2i;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedInvocationConstants;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PolygonUtilTests {

    @Test
    void createPolygonFromBlockPoints_whenGivenEmptyList_thenThrows() {
        assertThrows(IllegalArgumentException.class, () -> PolygonUtil.createPolygonFromBlockPoints(List.of()));
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenSinglePoint_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(5, 5));
        Polygon expected = new Polygon(
            new int[]{ 5, 6, 6, 5 },
            new int[]{ 5, 5, 6, 6 },
            4
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenHorizontalStrip_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(10, 10 ), new Vector2i(-20, 10));
        Polygon expected = new Polygon(
            new int[]{ 11, -20, -20, 11 },
            new int[]{ 10,  10,  11, 11 },
            4
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenHorizontalStripWithCollinearPoints_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(3, 10 ), new Vector2i(10, 10 ), new Vector2i(5, 10 ), new Vector2i(5, 10 ), new Vector2i(0, 10 ), new Vector2i(-20, 10), new Vector2i(2, 10 ), new Vector2i(3, 10 ));
        Polygon expected = new Polygon(
            new int[]{ 11, -20, -20, 11 },
            new int[]{ 10,  10,  11, 11 },
            4
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenVerticalStrip_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(-50, -60 ), new Vector2i(-50, 20));
        Polygon expected = new Polygon(
            new int[]{ -50, -49, -49, -50 },
            new int[]{ -60, -60,  21,  21 },
            4
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenDiagonalStrip_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(10, 10 ), new Vector2i(-5, -5));
        Polygon expected = new Polygon(
            new int[]{ -5, -4, 11, 11, 10, -5 },
            new int[]{ -5, -5, 10, 11, 11, -4 },
            6
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenClockwiseRectangle_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(0, 0), new Vector2i(10, 0), new Vector2i(10, 10), new Vector2i(0, 10));
        Polygon expected = new Polygon(
            new int[]{ 0, 11, 11,  0 },
            new int[]{ 0,  0, 11, 11 },
            4
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenCounterclockwiseRectangle_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(-20, 0), new Vector2i(200, 0), new Vector2i(200, -100), new Vector2i(-20, -100));
        Polygon expected = new Polygon(
            new int[]{ -20, 201,  201,  -20 },
            new int[]{   1,   1, -100, -100 },
            4
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenBlockyShape_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(
            new Vector2i(-5, -5), new Vector2i(5, -5), new Vector2i(5, 10), new Vector2i(10, 10), new Vector2i(10, 0), new Vector2i(20, 0),
            new Vector2i(20, 5), new Vector2i(30, 5), new Vector2i(30, 10), new Vector2i(30, 15), new Vector2i(25, 15), new Vector2i(25, 40),
            new Vector2i(0, 40), new Vector2i(-5, 40),
            new Vector2i(-5, 20)
        );
        Polygon expected = new Polygon(
            new int[]{ -5,  6,  6, 10, 10, 21, 21, 31, 31, 26, 26, -5 },
            new int[]{ -5, -5, 10, 10,  0,  0,  5,  5, 16, 16, 41, 41 },
            12
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @Test
    void createPolygonFromBlockPoints_whenGivenDiamondShape_thenReturnsCorrectPolygon() {
        List<Vector2i> input = List.of(new Vector2i(0, -5), new Vector2i(5, 0), new Vector2i(0, 5), new Vector2i(-5, 0));
        Polygon expected = new Polygon(
            new int[]{  0,  1, 6, 6, 1, 0, -5, -5 },
            new int[]{ -5, -5, 0, 1, 6, 6,  1,  0 },
            8
        );

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);
        assertPolygonEquals(expected, result);
    }

    @ParameterizedTest(name = ParameterizedInvocationConstants.DISPLAY_NAME_PLACEHOLDER + "[" + ParameterizedInvocationConstants.INDEX_PLACEHOLDER + "]")
    @MethodSource("provideArbitraryShapes")
    void createPolygonFromBlockPoints_whenGivenArbitraryShape_thenReturnedPolygonContainsAllDefiningBlocks(List<Vector2i> points) {
        List<Vector2i> input = points.stream().map(Vector2i::new).toList();

        Polygon result = PolygonUtil.createPolygonFromBlockPoints(input);

        for (Vector2i point : points) {
            assertTrue(result.contains(point.x, point.y, 1, 1), String.format("Polygon doesn't contain entire defining block (%d, %d).", point.x, point.y));
        }
    }

    private static Stream<Arguments> provideArbitraryShapes() {
        return Stream.of(
            Arguments.of(List.of(
                new Vector2i(0, 0), new Vector2i(10, 5), new Vector2i(15, 3),
                new Vector2i(20, -30), new Vector2i(13, -17), new Vector2i(15, -14),
                new Vector2i(10, -10), new Vector2i(7, -25), new Vector2i(-5, -10)
            )),
            Arguments.of(List.of(
                new Vector2i(-188, 292), new Vector2i(-221, 297), new Vector2i(-228, 281), new Vector2i(-234, 275),
                new Vector2i(-246, 268), new Vector2i(-256, 267), new Vector2i(-259, 259), new Vector2i(-258, 248),
                new Vector2i(-240, 245), new Vector2i(-206, 222), new Vector2i(-191, 208), new Vector2i(-183, 187),
                new Vector2i(-167, 170), new Vector2i(-142, 164), new Vector2i(-128, 165), new Vector2i(-99, 174),
                new Vector2i(-99, 182), new Vector2i(-92, 182), new Vector2i(-92, 174), new Vector2i(-88, 174),
                new Vector2i(-81, 187), new Vector2i(-87, 187), new Vector2i(-87, 194), new Vector2i(-79, 194),
                new Vector2i(-76, 208), new Vector2i(-80, 208), new Vector2i(-80, 255), new Vector2i(-172, 255),
                new Vector2i(-174, 262), new Vector2i(-171, 267), new Vector2i(-171, 280), new Vector2i(-174, 293)
            )),
            Arguments.of(List.of(
                new Vector2i(-255, 270), new Vector2i(-263, 271), new Vector2i(-272, 275), new Vector2i(-278, 282),
                new Vector2i(-282, 289), new Vector2i(-284, 299), new Vector2i(-282, 309), new Vector2i(-276, 318),
                new Vector2i(-269, 324), new Vector2i(-262, 327), new Vector2i(-255, 328), new Vector2i(-244, 326),
                new Vector2i(-236, 321), new Vector2i(-230, 313), new Vector2i(-226, 305), new Vector2i(-226, 299),
                new Vector2i(-229, 287), new Vector2i(-235, 278), new Vector2i(-243, 272), new Vector2i(-251, 270)
            )),
            Arguments.of(List.of(
                new Vector2i(-336, 189), new Vector2i(-347, 177), new Vector2i(-357, 177), new Vector2i(-356, 185),
                new Vector2i(-343, 195), new Vector2i(-339, 195), new Vector2i(-336, 192)
            )),
            Arguments.of(List.of(
                new Vector2i(-299, 296), new Vector2i(-325, 297), new Vector2i(-325, 325), new Vector2i(-299, 325)
            ))
        );
    }

    private static void assertPolygonEquals(Polygon expected, Polygon actual) {
        if (expected == actual) return;
        else if (expected == null || actual == null) failWithPolygonMismatch(expected, actual);

        if (expected.npoints != actual.npoints) failWithPolygonMismatch(expected, actual);
        if (expected.npoints == 0) return;

        // Try testing for equality starting from different points
        for (int offset = 0; offset < expected.npoints; offset++) {
            // Test same order
            {
                boolean success = true;

                for (int i = 0; i < expected.npoints; i++) {
                    if (expected.xpoints[(i + offset) % expected.npoints] != actual.xpoints[i] || expected.ypoints[(i + offset) % expected.npoints] != actual.ypoints[i]) {
                        success = false;
                        break;
                    }
                }

                if (success) return;
            }

            // Test reverse order
            {
                boolean success = true;

                for (int i = 0; i < expected.npoints; i++) {
                    int reversedIndex = expected.npoints - ((i + offset) % expected.npoints) - 1;
                    if (expected.xpoints[reversedIndex] != actual.xpoints[i] || expected.ypoints[reversedIndex] != actual.ypoints[i]) {
                        success = false;
                        break;
                    }
                }

                if (success) return;
            }
        }

        failWithPolygonMismatch(expected, actual);
    }

    private static void failWithPolygonMismatch(Polygon expected, Polygon actual) {
        fail(printPolygonMismatch(expected, actual));
    }

    private static String printPolygonMismatch(Polygon expected, Polygon actual) {
        return String.format("Polygon mismatch. Expected: %s. Actual: %s.", printPolygon(expected), printPolygon(actual));
    }

    private static String printPolygon(Polygon polygon) {
        if (polygon == null) return "null";

        StringBuilder builder = new StringBuilder();
        builder.append("[");

        for (int i = 0; i < polygon.npoints; i++) {
            if (i != 0) builder.append(", ");

            builder.append("(");
            builder.append(polygon.xpoints[i]);
            builder.append(", ");
            builder.append(polygon.ypoints[i]);
            builder.append(")");
        }

        builder.append("]");
        return builder.toString();
    }

}
