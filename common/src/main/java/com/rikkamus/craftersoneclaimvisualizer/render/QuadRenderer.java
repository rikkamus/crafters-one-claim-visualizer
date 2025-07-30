package com.rikkamus.craftersoneclaimvisualizer.render;

import org.joml.Vector3f;
import org.joml.Vector4f;

public interface QuadRenderer {

    void renderQuad(Vector3f bottomLeft, Vector3f bottomRight, Vector3f topRight, Vector3f topLeft, Vector4f rgba);

}
