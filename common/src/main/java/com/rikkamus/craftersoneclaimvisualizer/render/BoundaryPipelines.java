package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;

@UtilityClass
public class BoundaryPipelines {

    private static final RenderPipeline.Snippet BOUNDARY_SNIPPET;
    public static final RenderPipeline BOUNDARY_FILL_PIPELINE;
    public static final RenderPipeline BOUNDARY_OUTLINE_PIPELINE;

    static {
        BOUNDARY_SNIPPET = RenderPipeline.builder()
                                         .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
                                         .withUniform("Projection", UniformType.UNIFORM_BUFFER)
                                         .buildSnippet();

        BOUNDARY_FILL_PIPELINE = RenderPipeline.builder(BoundaryPipelines.BOUNDARY_SNIPPET)
                                               .withLocation(ResourceLocation.fromNamespaceAndPath(ClaimVisualizerMod.MOD_ID, "pipeline/boundary_fill"))
                                               .withVertexShader("core/position_color")
                                               .withFragmentShader("core/position_color")
                                               .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
                                               .withCull(false)
                                               .withBlend(BlendFunction.TRANSLUCENT)
                                               .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                               .withDepthWrite(false)
                                               .withDepthBias(-3f, -3f)
                                               .build();

        BOUNDARY_OUTLINE_PIPELINE = RenderPipeline.builder(BoundaryPipelines.BOUNDARY_SNIPPET)
                                                  .withLocation(ResourceLocation.fromNamespaceAndPath(ClaimVisualizerMod.MOD_ID, "pipeline/boundary_outline"))
                                                  .withVertexShader("core/position_color")
                                                  .withFragmentShader("core/position_color")
                                                  .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES)
                                                  .withCull(false)
                                                  .withBlend(BlendFunction.TRANSLUCENT)
                                                  .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST)
                                                  .withDepthWrite(false)
                                                  .withDepthBias(-3f, -3f)
                                                  .build();
    }

}
