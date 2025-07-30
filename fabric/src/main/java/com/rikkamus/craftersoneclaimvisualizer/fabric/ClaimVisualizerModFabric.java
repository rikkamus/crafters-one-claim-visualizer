package com.rikkamus.craftersoneclaimvisualizer.fabric;

import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.config.ClaimVisualizerConfig;
import com.rikkamus.craftersoneclaimvisualizer.config.ClothConfig;
import com.rikkamus.craftersoneclaimvisualizer.config.DefaultConfig;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
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

public final class ClaimVisualizerModFabric implements ClientModInitializer {

    private ClaimVisualizerMod mod;

    @Override
    public void onInitializeClient() {
        ClaimVisualizerConfig config;

        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            config = AutoConfig.register(ClothConfig.class, JanksonConfigSerializer::new).getConfig();
        } else {
            config = new DefaultConfig();
        }

        Version version = FabricLoader.getInstance().getModContainer(ClaimVisualizerMod.MOD_ID).orElseThrow().getMetadata().getVersion();
        this.mod = new ClaimVisualizerMod(config, version.getFriendlyString());

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
