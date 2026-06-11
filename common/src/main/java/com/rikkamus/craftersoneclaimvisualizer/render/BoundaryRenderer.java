package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rikkamus.craftersoneclaimvisualizer.Boundary;
import lombok.experimental.UtilityClass;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

@UtilityClass
public class BoundaryRenderer {

    private static final int UNIFORM_BUFFER_SIZE = new Std140SizeCalculator().putInt().get();

    public static void renderBoundaries(RenderContext context, Iterable<Boundary> boundaries, boolean applyFog) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(
            context.viewMatrix(),
            new Vector4f(1f, 1f, 1f, 1f),
            new Vector3f(),
            new Matrix4f()
        );

        try(GpuBuffer ubo = RenderSystem.getDevice().createBuffer(() -> context.name(), GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE, BoundaryRenderer.UNIFORM_BUFFER_SIZE)) {
            try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(ubo, false, true)) {
                Std140Builder.intoBuffer(view.data()).putInt(applyFog ? 1 : 0);
            }

            RenderPassBuilder.withPipeline(BoundaryPipelines.BOUNDARY_FILL_PIPELINE)
                             .withVertices(vertexConsumer -> {
                                 QuadRenderer renderer = (bottomLeft, bottomRight, topRight, topLeft, rgba) -> {
                                     renderQuadTriangles(vertexConsumer, bottomLeft, bottomRight, topRight, topLeft, rgba);
                                 };

                                 boundaries.forEach(boundary -> renderVerticalPrism(boundary.getBase(), boundary.getY1(), boundary.getY2(), boundary.getFillRgba(), renderer));
                             })
                             .withDefaultUniforms()
                             .withUniform("DynamicTransforms", dynamicTransforms)
                             .withUniform("BoundaryUniforms", ubo)
                             .renderToMainTarget(context.name());

            RenderPassBuilder.withPipeline(BoundaryPipelines.BOUNDARY_OUTLINE_PIPELINE)
                             .withVertices(vertexConsumer -> {
                                 QuadRenderer renderer = (bottomLeft, bottomRight, topRight, topLeft, rgba) -> {
                                     renderQuadOutline(vertexConsumer, bottomLeft, bottomRight, topRight, topLeft, rgba);
                                 };

                                 boundaries.forEach(boundary -> renderVerticalPrism(boundary.getBase(), boundary.getY1(), boundary.getY2(), boundary.getOutlineRgba(), renderer));
                             })
                             .withDefaultUniforms()
                             .withUniform("DynamicTransforms", dynamicTransforms)
                             .withUniform("BoundaryUniforms", ubo)
                             .renderToMainTarget(context.name());
        }
    }

    private static void renderVerticalPrism(Polygon base, float y1, float y2, Vector4f rgba, QuadRenderer renderer) {
        Vector2f first = null;
        Vector2f prev = null;

        for (int i = 0; i < base.npoints; i++) {
            Vector2f point = new Vector2f(base.xpoints[i], base.ypoints[i]);

            if (first == null) first = point;
            if (prev != null) renderVerticalQuad(prev, point, y1, y2, rgba, renderer);
            prev = point;
        }

        if (base.npoints > 2) {
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

    private static void renderQuadTriangles(VertexConsumer consumer, Vector3f bottomLeft, Vector3f bottomRight, Vector3f topRight, Vector3f topLeft, Vector4f rgba) {
        consumer.addVertex(bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(bottomRight.x, bottomRight.y, bottomRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);

        consumer.addVertex(topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(topLeft.x, topLeft.y, topLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
    }

    private static void renderQuadOutline(VertexConsumer consumer, Vector3f bottomLeft, Vector3f bottomRight, Vector3f topRight, Vector3f topLeft, Vector4f rgba) {
        consumer.addVertex(bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(bottomRight.x, bottomRight.y, bottomRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);

        consumer.addVertex(bottomRight.x, bottomRight.y, bottomRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w);

        consumer.addVertex(topRight.x, topRight.y, topRight.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(topLeft.x, topLeft.y, topLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w);

        consumer.addVertex(topLeft.x, topLeft.y, topLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
        consumer.addVertex(bottomLeft.x, bottomLeft.y, bottomLeft.z).setColor(rgba.x, rgba.y, rgba.z, rgba.w).setColor(rgba.x, rgba.y, rgba.z, rgba.w);
    }

}
