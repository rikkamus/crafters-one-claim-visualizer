package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rikkamus.craftersoneclaimvisualizer.Boundary;
import lombok.experimental.UtilityClass;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Collection;

@UtilityClass
public class BoundaryRenderer {

    public static void renderBoundaries(RenderContext context, Iterable<Boundary> boundaries) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(
            context.modelViewMatrix(),
            new Vector4f(1f, 1f, 1f, 1f),
            new Vector3f(),
            new Matrix4f(),
            1f
        );

        RenderPassBuilder.withPipeline(BoundaryPipelines.BOUNDARY_FILL_PIPELINE)
            .withVertices(vertexConsumer -> {
                QuadRenderer renderer = new CameraAwareQuadRenderer(context.cameraPos(), (bottomLeft, bottomRight, topRight, topLeft, rgba) -> {
                    renderQuadTriangles(vertexConsumer, context.pose(), bottomLeft, bottomRight, topRight, topLeft, rgba);
                });

                boundaries.forEach(boundary -> renderVerticalPrism(boundary.getPoints(), boundary.getY1(), boundary.getY2(), boundary.getFillRgba(), renderer));
            })
            .withDefaultUniforms()
            .withUniform("DynamicTransforms", dynamicTransforms)
            .renderToMainTarget(context.name());

        RenderPassBuilder.withPipeline(BoundaryPipelines.BOUNDARY_OUTLINE_PIPELINE)
            .withVertices(vertexConsumer -> {
                QuadRenderer renderer = new CameraAwareQuadRenderer(context.cameraPos(), (bottomLeft, bottomRight, topRight, topLeft, rgba) -> {
                    renderQuadOutline(vertexConsumer, context.pose(), bottomLeft, bottomRight, topRight, topLeft, rgba);
                });

                boundaries.forEach(boundary -> renderVerticalPrism(boundary.getPoints(), boundary.getY1(), boundary.getY2(), boundary.getOutlineRgba(), renderer));
            })
            .withDefaultUniforms()
            .withUniform("DynamicTransforms", dynamicTransforms)
            .renderToMainTarget(context.name());
    }

    private static void renderVerticalPrism(Collection<Vector2f> points, float y1, float y2, Vector4f rgba, QuadRenderer renderer) {
        Vector2f first = null;
        Vector2f prev = null;

        for (Vector2f point : points) {
            if (first == null) first = point;
            if (prev != null) renderVerticalQuad(prev, point, y1, y2, rgba, renderer);
            prev = point;
        }

        if (points.size() > 2) {
            renderVerticalQuad(prev, first, y1, y2, rgba, renderer);
        }
    }

    private static void renderVerticalQuad(Vector2f a, Vector2f b, float y1, float y2, Vector4f rgba, QuadRenderer renderer) {
        Vector3f bottomLeft = new Vector3f(a.x, y1, a.y);
        Vector3f bottomRight = new Vector3f(b.x, y1, b.y);
        Vector3f topRight = new Vector3f(b.x, y2, b.y);
        Vector3f topLeft = new Vector3f(a.x, y2, a.y);

        renderer.renderQuad(bottomLeft, bottomRight, topRight, topLeft, rgba);
    }

    private static void renderQuadTriangles(VertexConsumer consumer, PoseStack.Pose pose, Vector3f bottomLeft, Vector3f bottomRight, Vector3f topRight, Vector3f topLeft, Vector4f rgba) {
        consumer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(pose, bottomRight.x, bottomRight.y, bottomRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(pose, topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);

        consumer.addVertex(pose, topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(pose, topLeft.x, topLeft.y, topLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
    }

    private static void renderQuadOutline(VertexConsumer consumer, PoseStack.Pose pose, Vector3f bottomLeft, Vector3f bottomRight, Vector3f topRight, Vector3f topLeft, Vector4f rgba) {
        Vector3f quadNormal = bottomLeft.sub(bottomRight, new Vector3f()).cross(topRight.sub(bottomRight, new Vector3f()));

        consumer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);
        consumer.addVertex(pose, bottomRight.x, bottomRight.y, bottomRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);

        consumer.addVertex(pose, bottomRight.x, bottomRight.y, bottomRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);
        consumer.addVertex(pose, topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);

        consumer.addVertex(pose, topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);
        consumer.addVertex(pose, topLeft.x, topLeft.y, topLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);

        consumer.addVertex(pose, topLeft.x, topLeft.y, topLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);
        consumer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setNormal(pose, quadNormal);
    }

}
