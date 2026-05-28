package com.rikkamus.craftersoneclaimvisualizer.neoforge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.config.ClaimVisualizerConfig;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.joml.Matrix4f;

@Mod(value = ClaimVisualizerMod.MOD_ID, dist = Dist.CLIENT)
public class ClaimVisualizerModNeoForge {

    private ClaimVisualizerMod mod;

    public ClaimVisualizerModNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        ClaimVisualizerConfig config = ConfigBuilder.buildConfig(event.getContainer());

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

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.rotation(camera.rotation());
        viewMatrix.invert();
        viewMatrix.translate(camera.position().reverse().toVector3f());

        RenderContext context = new RenderContext(ClaimVisualizerMod.MOD_ID, viewMatrix);
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
