package com.rikkamus.craftersoneclaimvisualizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public class Boundary {

    private Polygon base;
    private float y1;
    private float y2;
    private Vector4f fillRgba;
    private Vector4f outlineRgba;

}
