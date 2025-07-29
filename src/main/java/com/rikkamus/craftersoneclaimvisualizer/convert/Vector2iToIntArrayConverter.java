package com.rikkamus.craftersoneclaimvisualizer.convert;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.joml.Vector2i;

public class Vector2iToIntArrayConverter extends StdConverter<Vector2i, int[]> {

    @Override
    public int[] convert(Vector2i value) {
        return new int[]{ value.x, value.y };
    }

}
