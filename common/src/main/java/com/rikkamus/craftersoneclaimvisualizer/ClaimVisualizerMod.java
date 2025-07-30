package com.rikkamus.craftersoneclaimvisualizer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import com.rikkamus.craftersoneclaimvisualizer.claim.ClaimRepository;
import com.rikkamus.craftersoneclaimvisualizer.claim.RepositoryFetchException;
import com.rikkamus.craftersoneclaimvisualizer.render.ClaimInfoOverlayRenderer;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public final class ClaimVisualizerMod {

    public static final String MOD_ID = "craftersoneclaimvisualizer";

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Config config;

    private ClaimRepository claimRepository;
    private CompletableFuture<Collection<Claim>> pendingClaimRequest = null;
    private ClaimManager claimManager = null;

    private boolean showClaims = false;

    public ClaimVisualizerMod(Config config, String version) {
        this.config = config;
        this.claimRepository = new ClaimRepository(String.format("Crafters.one Claim Visualizer Mod/%s", version.toString()), config.getApiRequestTimeout());
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("claims").then(Commands.literal("show").executes(this::onShowClaimsCommand)));
        dispatcher.register(Commands.literal("claims").then(Commands.literal("hide").executes(this::onHideClaimsCommand)));
        dispatcher.register(Commands.literal("claims").then(Commands.literal("refresh").executes(this::onRefreshClaimsCommand)));
    }

    private void loadClaims() {
        if (this.pendingClaimRequest != null) this.pendingClaimRequest.cancel(true);
        this.pendingClaimRequest = this.claimRepository.findAllClaims();
    }

    public void tick() {
        if (this.pendingClaimRequest != null && this.pendingClaimRequest.isDone()) {
            if (!this.pendingClaimRequest.isCancelled()) {
                try {
                    Collection<Claim> claims = this.pendingClaimRequest.join();

                    if (this.claimManager == null) this.claimManager = new ClaimManager();
                    this.claimManager.clearClaims();
                    this.claimManager.addAllClaims(claims);

                    ChatLogger.log("Claims loaded!", ChatFormatting.GREEN);
                } catch (Exception e) {
                    LOGGER.error("Failed to load claims.", e);

                    String errorMessage = e instanceof RepositoryFetchException fetchException ? fetchException.getMessage() : "Unknown error (check logs).";
                    ChatLogger.log(String.format("Failed to load claims: %s", errorMessage), ChatFormatting.RED);
                }
            }

            this.pendingClaimRequest = null;
        }
    }

    public void renderClaimBoundaries(RenderContext context) {
        if (!isRenderingClaims()) return;

        Profiler.get().push("claim_boundaries");
        this.claimManager.renderClaimBoundaries(context);
        Profiler.get().pop();
    }

    public void renderClaimInfoOverlay(GuiGraphics guiGraphics) {
        if (!isRenderingClaims()) return;
        if (Minecraft.getInstance().gui.getDebugOverlay().showDebugScreen()) return;

        Profiler.get().push("claim_info_overlay");

        Vec3 playerPos = Minecraft.getInstance().player.position();

        int x = this.config.getOverlayX();
        int y = this.config.getOverlayY();
        if (x < 0) x += guiGraphics.guiWidth();
        if (y < 0) y += guiGraphics.guiHeight();

        ClaimInfoOverlayRenderer.renderClaimOverlay(
            this.claimManager.getClaimAt(playerPos.x, playerPos.z).orElse(null),
            x,
            y,
            this.config.getOverlayHorizontalAlignment(),
            this.config.getOverlayVerticalAlignment(),
            guiGraphics
        );

        Profiler.get().pop();
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
