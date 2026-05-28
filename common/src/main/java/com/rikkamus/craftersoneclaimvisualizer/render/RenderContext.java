package com.rikkamus.craftersoneclaimvisualizer.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public record RenderContext(String name, Matrix4f transform, Vector3f cameraPos) {

}
