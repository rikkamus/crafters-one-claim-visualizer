package com.rikkamus.craftersoneclaimvisualizer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rikkamus.craftersoneclaimvisualizer.render.BoundaryRenderer;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

@Mod(ClaimVisualizerMod.MOD_ID)
public class ClaimVisualizerMod {

    public static final String MOD_ID = "craftersoneclaimvisualizer";
    public static final String MOD_VERSION = "0.1.0";

    public ClaimVisualizerMod() {
        NeoForge.EVENT_BUS.addListener(this::onParticlesRendered);
    }

    private void onParticlesRendered(RenderLevelStageEvent.AfterParticles event) {
        PoseStack stack = event.getPoseStack();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        Vector4f fillRgba = new Vector4f(14f / 255f, 233f / 255f, 125f / 255f, 0.2f);
        Vector4f outlineRgba = new Vector4f(14f / 255f, 233f / 255f, 125f / 255f, 0.8f);

        RenderContext context = new RenderContext(ClaimVisualizerMod.MOD_ID, stack.last(), camera.position().toVector3f(), event.getModelViewMatrix());
        List<Boundary> boundaries = List.of(
            new Boundary(List.of(new Vector2f(-1f, -1f), new Vector2f(1f, 1f), new Vector2f(2f, 0f)), -1f, 1f, fillRgba, outlineRgba),
            new Boundary(List.of(new Vector2f(-1f, -3f), new Vector2f(1f, -1f), new Vector2f(2f, -2f)), -1f, 1f, fillRgba, outlineRgba)
        );

        BoundaryRenderer.renderBoundaries(context, boundaries);
    }

}
