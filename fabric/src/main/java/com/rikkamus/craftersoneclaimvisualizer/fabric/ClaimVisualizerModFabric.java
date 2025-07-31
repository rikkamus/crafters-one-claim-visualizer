package com.rikkamus.craftersoneclaimvisualizer.fabric;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.config.ClaimVisualizerConfig;
import com.rikkamus.craftersoneclaimvisualizer.config.ClothConfig;
import com.rikkamus.craftersoneclaimvisualizer.config.DefaultConfig;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
            dispatcher.register(ClientCommandManager.literal("claims").then(ClientCommandManager.literal("show").executes(this::onShowClaimsCommand)));
            dispatcher.register(ClientCommandManager.literal("claims").then(ClientCommandManager.literal("hide").executes(this::onHideClaimsCommand)));
            dispatcher.register(ClientCommandManager.literal("claims").then(ClientCommandManager.literal("refresh").executes(this::onRefreshClaimsCommand)));
        });
    }

    private int onShowClaimsCommand(CommandContext<FabricClientCommandSource> context) {
        this.mod.showClaims();
        return Command.SINGLE_SUCCESS;
    }

    private int onHideClaimsCommand(CommandContext<FabricClientCommandSource> context) {
        this.mod.hideClaims();
        return Command.SINGLE_SUCCESS;
    }

    private int onRefreshClaimsCommand(CommandContext<FabricClientCommandSource> context) {
        this.mod.refreshClaims();
        return Command.SINGLE_SUCCESS;
    }

}
