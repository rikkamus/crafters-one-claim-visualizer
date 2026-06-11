package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import net.minecraft.client.Minecraft;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.lang.Math;
import java.util.*;
import java.util.List;

public class BoundaryRenderer implements AutoCloseable {

    private static final int UNIFORM_BUFFER_SIZE = new Std140SizeCalculator().putFloat().putFloat().putVec4().putInt().get();

    private GpuBuffer ubo;

    private MeshData rawFillMesh;
    private MeshData rawOutlineMesh;
    private MeshData correctedFillMesh;
    private MeshData correctedOutlineMesh;

    private GpuBuffer rawFillVbo;
    private GpuBuffer rawOutlineVbo;
    private GpuBuffer correctedFillVbo;
    private GpuBuffer correctedOutlineVbo;

    private List<Boundary> boundaries;

    public BoundaryRenderer() {
        this.ubo = RenderSystem.getDevice().createBuffer(() -> "ClaimBoundaryRendererUbo", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, BoundaryRenderer.UNIFORM_BUFFER_SIZE);
    }

    public void render(
        RenderContext context,
        float boundaryMinY,
        float boundaryMaxY,
        Vector3fc defaultRgb,
        float fillOpacity,
        float outlineOpacity,
        boolean useCorrectedBoundaries,
        boolean forceDefaultColor,
        boolean applyFog
    ) {
        if (this.ubo == null || this.rawFillVbo == null || this.rawOutlineVbo == null || this.correctedFillVbo == null || this.correctedOutlineVbo == null || this.boundaries == null) return;

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(
            context.viewMatrix(),
            new Vector4f(1f, 1f, 1f, 1f),
            new Vector3f(),
            new Matrix4f()
        );

        int fillOffset = 0;
        int outlineOffset = 0;

        for (Boundary boundary : this.boundaries) {
            Vector3fc rgb = forceDefaultColor ? defaultRgb : Objects.requireNonNullElse(boundary.rgb(), defaultRgb);
            Vector4f fillRgba = new Vector4f(rgb.x(), rgb.y(), rgb.z(), fillOpacity);
            Vector4f outlineRgba = new Vector4f(rgb.x(), rgb.y(), rgb.z(), outlineOpacity);

            int pointCount = useCorrectedBoundaries ? boundary.correctedPointCount() : boundary.rawPointCount();
            int fillVertexCount = Math.max(0, pointCount > 2 ? pointCount * 6 : (pointCount - 1) * 6);
            int outlineVertexCount = Math.max(0, pointCount > 2 ? pointCount * 8 : (pointCount - 1) * 8);

            try (final MemoryStack stack = MemoryStack.stackPush()) {
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(
                    this.ubo.slice(),
                    Std140Builder.onStack(stack, BoundaryRenderer.UNIFORM_BUFFER_SIZE)
                                 .putFloat(boundaryMinY)
                                 .putFloat(boundaryMaxY)
                                 .putVec4(fillRgba)
                                 .putInt(applyFog ? 1 : 0)
                                 .get()
                );
            }

            try (RenderPass renderPass = RenderUtil.createRenderPass("ClaimBoundaryFillRenderPass", Minecraft.getInstance().getMainRenderTarget())) {
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", dynamicTransforms);
                renderPass.setUniform("BoundaryUniforms", this.ubo);

                renderPass.setVertexBuffer(0, useCorrectedBoundaries ? this.correctedFillVbo : this.rawFillVbo);
                renderPass.setPipeline(BoundaryPipelines.BOUNDARY_FILL_PIPELINE);
                renderPass.draw(fillOffset, fillVertexCount);
            }

            try (final MemoryStack stack = MemoryStack.stackPush()) {
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(
                    this.ubo.slice(),
                    Std140Builder.onStack(stack, BoundaryRenderer.UNIFORM_BUFFER_SIZE)
                                 .putFloat(boundaryMinY)
                                 .putFloat(boundaryMaxY)
                                 .putVec4(outlineRgba)
                                 .putInt(applyFog ? 1 : 0)
                                 .get()
                );
            }

            try (RenderPass renderPass = RenderUtil.createRenderPass("ClaimBoundaryOutlineRenderPass", Minecraft.getInstance().getMainRenderTarget())) {
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", dynamicTransforms);
                renderPass.setUniform("BoundaryUniforms", this.ubo);

                renderPass.setVertexBuffer(0, useCorrectedBoundaries ? this.correctedOutlineVbo : this.rawOutlineVbo);
                renderPass.setPipeline(BoundaryPipelines.BOUNDARY_OUTLINE_PIPELINE);
                renderPass.draw(outlineOffset, outlineVertexCount);
            }

            fillOffset += fillVertexCount;
            outlineOffset += outlineVertexCount;
        }
    }

    public void setClaims(Collection<Claim> claims) {
        reset();
        if (claims == null) return;

        Collection<Polygon> rawPolygons = claims.stream().map(Claim::getRawShape).toList();
        Collection<Polygon> correctedPolygons = claims.stream().map(Claim::getCorrectedShape).toList();

        this.rawFillMesh = createFillMeshData(rawPolygons);
        this.rawOutlineMesh = createOutlineMeshData(rawPolygons);

        this.correctedFillMesh = createFillMeshData(correctedPolygons);
        this.correctedOutlineMesh = createOutlineMeshData(correctedPolygons);

        this.rawFillVbo = RenderSystem.getDevice().createBuffer(() -> "ClaimBoundaryRawFillVbo", GpuBuffer.USAGE_VERTEX, this.rawFillMesh.vertexBuffer());
        this.rawOutlineVbo = RenderSystem.getDevice().createBuffer(() -> "ClaimBoundaryRawOutlineVbo", GpuBuffer.USAGE_VERTEX, this.rawOutlineMesh.vertexBuffer());
        this.correctedFillVbo = RenderSystem.getDevice().createBuffer(() -> "ClaimBoundaryCorrectedFillVbo", GpuBuffer.USAGE_VERTEX, this.correctedFillMesh.vertexBuffer());
        this.correctedOutlineVbo = RenderSystem.getDevice().createBuffer(() -> "ClaimBoundaryCorrectedOutlineVbo", GpuBuffer.USAGE_VERTEX, this.correctedOutlineMesh.vertexBuffer());

        this.boundaries = claims.stream().map(claim -> new Boundary(
            claim.getRgb() != null ? new Vector3f(claim.getRgb()) : null,
            claim.getRawShape().npoints,
            claim.getCorrectedShape().npoints
        )).toList();
    }

    @Override
    public void close() {
        this.ubo.close();
        this.ubo = null;
        reset();
    }

    private MeshData createFillMeshData(Collection<Polygon> polygons) {
        BufferBuilder fillBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

        for (Polygon polygon : polygons) {
            Vector2f first = null;
            Vector2f prev = null;

            for (int i = 0; i < polygon.npoints; i++) {
                Vector2f point = new Vector2f(polygon.xpoints[i], polygon.ypoints[i]);

                if (first == null) first = point;

                if (prev != null) {
                    fillBuilder.addVertex(prev.x, 0, prev.y);
                    fillBuilder.addVertex(point.x, 0, point.y);
                    fillBuilder.addVertex(point.x, 1, point.y);

                    fillBuilder.addVertex(point.x, 1, point.y);
                    fillBuilder.addVertex(prev.x, 1, prev.y);
                    fillBuilder.addVertex(prev.x, 0, prev.y);
                }

                prev = point;
            }

            if (polygon.npoints > 2) {
                fillBuilder.addVertex(prev.x, 0, prev.y);
                fillBuilder.addVertex(first.x, 0, first.y);
                fillBuilder.addVertex(first.x, 1, first.y);

                fillBuilder.addVertex(first.x, 1, first.y);
                fillBuilder.addVertex(prev.x, 1, prev.y);
                fillBuilder.addVertex(prev.x, 0, prev.y);
            }
        }

        return fillBuilder.build();
    }

    private MeshData createOutlineMeshData(Collection<Polygon> polygons) {
        BufferBuilder outlineBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);

        for (Polygon polygon : polygons) {
            Vector2f first = null;
            Vector2f prev = null;

            for (int i = 0; i < polygon.npoints; i++) {
                Vector2f point = new Vector2f(polygon.xpoints[i], polygon.ypoints[i]);

                if (first == null) first = point;

                if (prev != null) {
                    outlineBuilder.addVertex(prev.x, 0, prev.y);
                    outlineBuilder.addVertex(point.x, 0, point.y);

                    outlineBuilder.addVertex(point.x, 0, point.y);
                    outlineBuilder.addVertex(point.x, 1, point.y);

                    outlineBuilder.addVertex(point.x, 1, point.y);
                    outlineBuilder.addVertex(prev.x, 1, prev.y);

                    outlineBuilder.addVertex(prev.x, 1, prev.y);
                    outlineBuilder.addVertex(prev.x, 0, prev.y);
                }

                prev = point;
            }

            if (polygon.npoints > 2) {
                outlineBuilder.addVertex(prev.x, 0, prev.y);
                outlineBuilder.addVertex(first.x, 0, first.y);

                outlineBuilder.addVertex(first.x, 0, first.y);
                outlineBuilder.addVertex(first.x, 1, first.y);

                outlineBuilder.addVertex(first.x, 1, first.y);
                outlineBuilder.addVertex(prev.x, 1, prev.y);

                outlineBuilder.addVertex(prev.x, 1, prev.y);
                outlineBuilder.addVertex(prev.x, 0, prev.y);
            }
        }

        return outlineBuilder.build();
    }

    private void reset() {
        if (this.rawFillMesh != null) {
            this.rawFillMesh.close();
            this.rawFillMesh = null;
        }

        if (this.rawOutlineMesh != null) {
            this.rawOutlineMesh.close();
            this.rawOutlineMesh = null;
        }

        if (this.correctedFillMesh != null) {
            this.correctedFillMesh.close();
            this.correctedFillMesh = null;
        }

        if (this.correctedOutlineMesh != null) {
            this.correctedOutlineMesh.close();
            this.correctedOutlineMesh = null;
        }

        if (this.rawFillVbo != null) {
            this.rawFillVbo.close();
            this.rawFillVbo = null;
        }

        if (this.rawOutlineVbo != null) {
            this.rawOutlineVbo.close();
            this.rawOutlineVbo = null;
        }

        if (this.correctedFillVbo != null) {
            this.correctedFillVbo.close();
            this.correctedFillVbo = null;
        }

        if (this.correctedOutlineVbo != null) {
            this.correctedOutlineVbo.close();
            this.correctedOutlineVbo = null;
        }

        this.boundaries = null;
    }

}
