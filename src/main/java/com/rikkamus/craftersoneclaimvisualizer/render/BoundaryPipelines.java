package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

@UtilityClass
public class BoundaryPipelines {

    public static final RenderPipeline BOUNDARY_FILL_PIPELINE;
    public static final RenderPipeline BOUNDARY_OUTLINE_PIPELINE;

    static {
        BOUNDARY_FILL_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
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

        BOUNDARY_OUTLINE_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
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
