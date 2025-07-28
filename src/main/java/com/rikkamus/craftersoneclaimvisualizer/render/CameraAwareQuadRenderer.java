package com.rikkamus.craftersoneclaimvisualizer.render;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class CameraAwareQuadRenderer implements QuadRenderer {

    private Vector3f cameraPos;
    private QuadRenderer quadRenderer;

    public CameraAwareQuadRenderer(Vector3f cameraPos, QuadRenderer quadRenderer) {
        this.cameraPos = cameraPos;
        this.quadRenderer = quadRenderer;
    }

    @Override
    public void renderQuad(Vector3f bottomLeft, Vector3f bottomRight, Vector3f topRight, Vector3f topLeft, Vector4f rgba) {
        this.quadRenderer.renderQuad(
            bottomLeft.sub(this.cameraPos, new Vector3f()),
            bottomRight.sub(this.cameraPos, new Vector3f()),
            topRight.sub(this.cameraPos, new Vector3f()),
            topLeft.sub(this.cameraPos, new Vector3f()),
            rgba
        );
    }

    public Vector3f getCameraPos() {
        return this.cameraPos;
    }

    public void setCameraPos(Vector3f cameraPos) {
        this.cameraPos = cameraPos;
    }

    public QuadRenderer getQuadRenderer() {
        return this.quadRenderer;
    }

    public void setQuadRenderer(QuadRenderer quadRenderer) {
        this.quadRenderer = quadRenderer;
    }

}
