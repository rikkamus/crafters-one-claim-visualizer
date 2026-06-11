package com.rikkamus.craftersoneclaimvisualizer.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@UtilityClass
public class RenderUtil {

    public static RenderPass createRenderPass(String name, RenderTarget renderTarget) {
        return RenderSystem.getDevice().createCommandEncoder().createRenderPass(
            () -> name,
            renderTarget.getColorTextureView(),
            OptionalInt.empty(),
            renderTarget.getDepthTextureView(),
            OptionalDouble.empty()
        );
    }

}
