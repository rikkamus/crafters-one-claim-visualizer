package com.rikkamus.craftersoneclaimvisualizer.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Getter
@Setter
@AllArgsConstructor
public class CameraAwareQuadRenderer implements QuadRenderer {

    private Vector3f cameraPos;
    private QuadRenderer quadRenderer;

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

}
