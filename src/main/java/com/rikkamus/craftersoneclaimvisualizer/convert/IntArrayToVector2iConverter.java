package com.rikkamus.craftersoneclaimvisualizer.convert;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.joml.Vector2i;

public class IntArrayToVector2iConverter extends StdConverter<int[], Vector2i> {

    @Override
    public Vector2i convert(int[] value) {
        if (value.length != 2) throw new IllegalArgumentException("Vector2i requires exactly two coordinates.");
        return new Vector2i(value[0], value[1]);
    }

}
