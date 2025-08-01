package com.rikkamus.craftersoneclaimvisualizer.neoforge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.config.ClaimVisualizerConfig;
import com.rikkamus.craftersoneclaimvisualizer.config.ClothConfig;
import com.rikkamus.craftersoneclaimvisualizer.config.DefaultConfig;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.maven.artifact.versioning.ArtifactVersion;

@Mod(value = ClaimVisualizerMod.MOD_ID, dist = Dist.CLIENT)
public class ClaimVisualizerModNeoForge {

    private ClaimVisualizerMod mod;

    public ClaimVisualizerModNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        ClaimVisualizerConfig config;

        if (ModList.get().isLoaded(ClothConfigInitializer.MOD_ID)) {
            ConfigHolder<ClothConfig> holder = AutoConfig.register(ClothConfig.class, JanksonConfigSerializer::new);
            holder.registerSaveListener((configHolder, clothConfig) -> clothConfig.validate());
            config = holder.getConfig();

            event.getContainer().registerExtensionPoint(
                IConfigScreenFactory.class,
                (modContainer, parent) -> AutoConfig.getConfigScreen(ClothConfig.class, parent).get()
            );
        } else {
            config = new DefaultConfig();
        }

        ArtifactVersion version = event.getContainer().getModInfo().getVersion();
        this.mod = new ClaimVisualizerMod(config, version.toString());
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
        event.getDispatcher().register(Commands.literal("claims").then(Commands.literal("show").executes(this::onShowClaimsCommand)));
        event.getDispatcher().register(Commands.literal("claims").then(Commands.literal("hide").executes(this::onHideClaimsCommand)));
        event.getDispatcher().register(Commands.literal("claims").then(Commands.literal("refresh").executes(this::onRefreshClaimsCommand)));
    }

    private int onShowClaimsCommand(CommandContext<CommandSourceStack> context) {
        this.mod.showClaims();
        return Command.SINGLE_SUCCESS;
    }

    private int onHideClaimsCommand(CommandContext<CommandSourceStack> context) {
        this.mod.hideClaims();
        return Command.SINGLE_SUCCESS;
    }

    private int onRefreshClaimsCommand(CommandContext<CommandSourceStack> context) {
        this.mod.refreshClaims();
        return Command.SINGLE_SUCCESS;
    }

}
