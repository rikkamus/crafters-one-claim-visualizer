package com.rikkamus.craftersoneclaimvisualizer.fabric;

import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.Config;
import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public final class ClaimVisualizerModFabric implements ClientModInitializer {

    private ClaimVisualizerMod mod;

    @Override
    public void onInitializeClient() {
        Version version = FabricLoader.getInstance().getModContainer(ClaimVisualizerMod.MOD_ID).orElseThrow().getMetadata().getVersion();
        this.mod = new ClaimVisualizerMod(new Config() {
            @Override
            public Duration getApiRequestTimeout() {
                return Duration.of(5, ChronoUnit.SECONDS);
            }

            @Override
            public int getOverlayX() {
                return 5;
            }

            @Override
            public int getOverlayY() {
                return 5;
            }

            @Override
            public Alignment getOverlayHorizontalAlignment() {
                return Alignment.START;
            }

            @Override
            public Alignment getOverlayVerticalAlignment() {
                return Alignment.START;
            }
        }, version.getFriendlyString());

        ClientTickEvents.START_CLIENT_TICK.register(client -> this.mod.tick());

        WorldRenderEvents.END.register(worldContext -> {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            RenderContext context = new RenderContext(ClaimVisualizerMod.MOD_ID, worldContext.matrixStack().last(), camera.position().toVector3f(), worldContext.positionMatrix());
            this.mod.renderClaimBoundaries(context);
        });

        HudElementRegistry.addLast(
            ResourceLocation.fromNamespaceAndPath(ClaimVisualizerMod.MOD_ID, "hud/claim_info_overlay"),
            (context, tickCounter) -> this.mod.renderClaimInfoOverlay(context)
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            this.mod.registerCommands(dispatcher);
        });
    }

}
