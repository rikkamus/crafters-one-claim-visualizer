package com.rikkamus.craftersoneclaimvisualizer.neoforge;

import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.maven.artifact.versioning.ArtifactVersion;

@Mod(value = ClaimVisualizerMod.MOD_ID, dist = Dist.CLIENT)
public class ClaimVisualizerModNeoForge {

    private ClaimVisualizerMod mod;

    public ClaimVisualizerModNeoForge(ModContainer container, IEventBus modEventBus) {
        container.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.getSpec());

        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        ArtifactVersion version = event.getContainer().getModInfo().getVersion();
        this.mod = new ClaimVisualizerMod(NeoForgeConfig.getInstance(), version.toString());
    }

    @SubscribeEvent
    private void onClientTick(ClientTickEvent.Pre event) {
        this.mod.tick();
    }

    @SubscribeEvent
    private void onLevelRendered(RenderLevelStageEvent.AfterLevel event) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        RenderContext context = new RenderContext(ClaimVisualizerMod.MOD_ID, event.getPoseStack().last(), camera.position().toVector3f(), event.getModelViewMatrix());
        this.mod.renderClaimBoundaries(context);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private void onGuiRendered(RenderGuiEvent.Post event) {
        this.mod.renderClaimInfoOverlay(event.getGuiGraphics());
    }

    @SubscribeEvent
    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        this.mod.registerCommands(event.getDispatcher());
    }

}
