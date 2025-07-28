package com.rikkamus.craftersoneclaimvisualizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class Boundary {

    private Collection<Vector2f> points;
    private float y1;
    private float y2;
    private Vector4f fillRgba;
    private Vector4f outlineRgba;

}
