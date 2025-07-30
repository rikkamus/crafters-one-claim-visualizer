package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public record RenderContext(String name, PoseStack.Pose pose, Vector3f cameraPos, Matrix4fc modelViewMatrix) {

}
