package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;

public class RenderPassBuilder {

    public static RenderPassBuilder withPipeline(RenderPipeline pipeline) {
        return new RenderPassBuilder(pipeline);
    }

    private static <T> Consumer<T> andThen(Consumer<T> first, Consumer<T> second) {
        if (first == null) return second;
        return first.andThen(second);
    }

    private final RenderPipeline pipeline;
    private Consumer<VertexConsumer> vertexCreator;
    private Consumer<RenderPass> uniformSetter;

    public RenderPassBuilder(RenderPipeline pipeline) {
        this.pipeline = pipeline;
        this.vertexCreator = null;
        this.uniformSetter = null;
    }

    public RenderPassBuilder withVertices(Consumer<VertexConsumer> vertexCreator) {
        this.vertexCreator = RenderPassBuilder.andThen(this.vertexCreator, vertexCreator);
        return this;
    }

    public RenderPassBuilder withDefaultUniforms() {
        this.uniformSetter = RenderPassBuilder.andThen(this.uniformSetter, RenderSystem::bindDefaultUniforms);
        return this;
    }

    public RenderPassBuilder withUniform(String name, GpuBufferSlice buffer) {
        this.uniformSetter = RenderPassBuilder.andThen(this.uniformSetter, renderPass -> renderPass.setUniform(name, buffer));
        return this;
    }

    public void renderToMainTarget(String name) {
        render(name, Minecraft.getInstance().getMainRenderTarget());
    }

    public void render(String name, RenderTarget renderTarget) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(this.pipeline.getVertexFormatMode(), this.pipeline.getVertexFormat());
        if (this.vertexCreator != null) this.vertexCreator.accept(bufferBuilder);

        try (MeshData meshData = bufferBuilder.build()) {
            // Ignore empty buffer builder
            if (meshData == null) return;

            try (

                RenderPass renderPass = createRenderPass(name, renderTarget);
                GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(() -> name, GpuBuffer.USAGE_VERTEX, meshData.vertexBuffer())
            ) {
                renderPass.setPipeline(this.pipeline);
                if (this.uniformSetter != null) this.uniformSetter.accept(renderPass);

                RenderSystem.AutoStorageIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(this.pipeline.getVertexFormatMode());
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.setIndexBuffer(indexBuffer.getBuffer(meshData.drawState().indexCount()), indexBuffer.type());
                renderPass.draw(0, meshData.drawState().indexCount());
            }
        }
    }

    private RenderPass createRenderPass(String name, RenderTarget renderTarget) {
        return RenderSystem.getDevice().createCommandEncoder().createRenderPass(
            () -> name,
            renderTarget.getColorTextureView(),
            OptionalInt.empty(),
            renderTarget.getDepthTextureView(),
            OptionalDouble.empty()
        );
    }

}
