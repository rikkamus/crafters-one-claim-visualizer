package com.rikkamus.craftersoneclaimvisualizer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.rikkamus.craftersoneclaimvisualizer.claim.ClaimRepository;
import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import com.rikkamus.craftersoneclaimvisualizer.claim.RepositoryFetchException;
import com.rikkamus.craftersoneclaimvisualizer.render.ClaimInfoOverlayRenderer;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mod(ClaimVisualizerMod.MOD_ID)
public class ClaimVisualizerMod {

    public static final String MOD_ID = "craftersoneclaimvisualizer";

    private static final Logger LOGGER = LogUtils.getLogger();

    private final ClaimRepository claimRepository;
    private CompletableFuture<Collection<Claim>> pendingClaimRequest = null;
    private ClaimManager claimManager = null;

    private boolean showClaims = false;

    public ClaimVisualizerMod(ModContainer container) {
        this.claimRepository = new ClaimRepository(String.format("Crafters.one Claim Visualizer Mod/%s", container.getModInfo().getVersion().toString()));

        NeoForge.EVENT_BUS.register(this);
    }

    private void loadClaims() {
        if (this.pendingClaimRequest != null) this.pendingClaimRequest.cancel(true);
        this.pendingClaimRequest = this.claimRepository.findAllClaims();
    }

    @SubscribeEvent
    private void onClientTick(ClientTickEvent.Pre event) {
        if (this.pendingClaimRequest != null && this.pendingClaimRequest.isDone()) {
            if (!this.pendingClaimRequest.isCancelled()) {
                try {
                    Collection<Claim> claims = this.pendingClaimRequest.join();

                    if (this.claimManager == null) this.claimManager = new ClaimManager();
                    this.claimManager.clearClaims();
                    this.claimManager.addAllClaims(claims);

                    ChatLogger.log("Claims loaded!", ChatFormatting.GREEN);
                } catch (Exception e) {
                    ClaimVisualizerMod.LOGGER.error("Failed to load claims.", e);

                    String errorMessage = e instanceof RepositoryFetchException fetchException ? fetchException.getMessage() : "Unknown error (check logs).";
                    ChatLogger.log(String.format("Failed to load claims: %s", errorMessage), ChatFormatting.RED);
                }
            }

            this.pendingClaimRequest = null;
        }
    }

    @SubscribeEvent
    private void onLevelRendered(RenderLevelStageEvent.AfterLevel event) {
        if (!isRenderingClaims()) return;

        Profiler.get().push("claim_boundaries");

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        RenderContext context = new RenderContext(ClaimVisualizerMod.MOD_ID, event.getPoseStack().last(), camera.position().toVector3f(), event.getModelViewMatrix());

        this.claimManager.renderClaimBoundaries(context);

        Profiler.get().pop();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private void onGuiRendered(RenderGuiEvent.Post event) {
        if (!isRenderingClaims()) return;
        if (Minecraft.getInstance().gui.getDebugOverlay().showDebugScreen()) return;

        Profiler.get().push("claim_info_overlay");

        Vec3 playerPos = Minecraft.getInstance().player.position();
        ClaimInfoOverlayRenderer.renderClaimOverlay(this.claimManager.getClaimAt(playerPos.x, playerPos.z).orElse(null), event.getGuiGraphics());

        Profiler.get().pop();
    }

    @SubscribeEvent
    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("claims").then(Commands.literal("show").executes(this::onShowClaimsCommand)));
        event.getDispatcher().register(Commands.literal("claims").then(Commands.literal("hide").executes(this::onHideClaimsCommand)));
        event.getDispatcher().register(Commands.literal("claims").then(Commands.literal("refresh").executes(this::onRefreshClaimsCommand)));
    }

    private int onShowClaimsCommand(CommandContext<CommandSourceStack> context) {
        if (this.claimManager == null) loadClaims();
        this.showClaims = true;
        return Command.SINGLE_SUCCESS;
    }

    private int onHideClaimsCommand(CommandContext<CommandSourceStack> context) {
        this.showClaims = false;
        return Command.SINGLE_SUCCESS;
    }

    private int onRefreshClaimsCommand(CommandContext<CommandSourceStack> context) {
        loadClaims();
        return Command.SINGLE_SUCCESS;
    }

    private boolean isRenderingClaims() {
        return this.showClaims &&
            this.claimManager != null &&
            Minecraft.getInstance().player != null &&
            Minecraft.getInstance().player.level().dimension() == Level.OVERWORLD &&
            !Minecraft.getInstance().options.hideGui;
    }

}
