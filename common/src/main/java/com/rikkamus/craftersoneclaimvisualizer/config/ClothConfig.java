package com.rikkamus.craftersoneclaimvisualizer.config;

import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.Color;
import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.world.InteractionResult;
import org.joml.Vector3f;

import java.net.URI;
import java.time.Duration;

@Config(name = ClaimVisualizerMod.MOD_ID)
public class ClothConfig implements ClaimVisualizerConfig, ConfigData {

    @ConfigEntry.Category("claimboundaries")
    @Comment("""
        Enables boundary correction to ensure that all claim-defining blocks are fully contained within the boundary.
        May cause some claims to overlap.""")
    private boolean claimBoundaryCorrectionEnabled = DefaultConfig.DEFAULT_BOUNDARY_CORRECTION_ENABLED;

    @ConfigEntry.Category("claimboundaries")
    @Comment("The lowest altitude at which claim boundaries should be rendered.")
    private int claimBoundaryMinY = DefaultConfig.DEFAULT_BOUNDARY_MIN_Y;

    @ConfigEntry.Category("claimboundaries")
    @Comment("The highest altitude at which claim boundaries should be rendered.")
    private int claimBoundaryMaxY = DefaultConfig.DEFAULT_BOUNDARY_MAX_Y;

    @ConfigEntry.Category("claimboundaries")
    @ConfigEntry.ColorPicker
    @Comment("The default color to use for claims that don't have a custom color specified.")
    private int defaultClaimBoundaryColor = DefaultConfig.DEFAULT_BOUNDARY_COLOR;

    @ConfigEntry.Category("claimboundaries")
    @Comment("Forces the use of the default boundary color for all claims, overriding any custom colors.")
    private boolean defaultClaimBoundaryColorForced = DefaultConfig.DEFAULT_FORCE_DEFAULT_BOUNDARY_COLOR;

    @ConfigEntry.Category("claimboundaries")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    @Comment("Opacity of claim boundary sides, specified as a percentage value between 0 and 100.")
    private int claimBoundaryFillOpacity = (int) (DefaultConfig.DEFAULT_BOUNDARY_FILL_OPACITY * 100f);

    @ConfigEntry.Category("claimboundaries")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    @Comment("Opacity of claim boundary outlines, specified as a percentage value between 0 and 100.")
    private int claimBoundaryOutlineOpacity = (int) (DefaultConfig.DEFAULT_BOUNDARY_OUTLINE_OPACITY * 100f);

    @ConfigEntry.Category("claiminfooverlay")
    @Comment("""
        Positive values define distance from the left edge of the screen.
        Negative values define distance from the right edge of the screen.""")
    private int overlayX = DefaultConfig.DEFAULT_OVERLAY_X;

    @ConfigEntry.Category("claiminfooverlay")
    @Comment("""
        Positive values define distance from the top edge of the screen.
        Negative values define distance from the bottom edge of the screen.""")
    private int overlayY = DefaultConfig.DEFAULT_OVERLAY_Y;

    @ConfigEntry.Category("claiminfooverlay")
    @Comment("""
        Specifies how text should be aligned horizontally.
        Allowed values: START, CENTER, END""")
    private Alignment overlayHorizontalAlignment = DefaultConfig.DEFAULT_OVERLAY_HORIZONTAL_ALIGNMENT;

    @ConfigEntry.Category("claiminfooverlay")
    @Comment("""
        Specifies how text should be aligned vertically.
        Allowed values: START, CENTER, END""")
    private Alignment overlayVerticalAlignment = DefaultConfig.DEFAULT_OVERLAY_VERTICAL_ALIGNMENT;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Must be between 100 and 60000 milliseconds.")
    private long apiRequestTimeoutMillis = DefaultConfig.DEFAULT_REQUEST_TIMEOUT.toMillis();

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Allows overriding the API endpoint used to fetch claims.")
    private boolean overrideApiEndpoint = false;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.RequiresRestart
    @Comment("""
        Custom API endpoint URI to use if overriding is enabled.
        Must use either HTTP or HTTPS as the scheme.""")
    private String customApiEndpointUri = "https://example.com";

    @Override
    public void validatePostLoad() {
        validate();
    }

    public InteractionResult validate() {
        if (this.claimBoundaryMinY > this.claimBoundaryMaxY) {
            ClaimVisualizerMod.LOGGER.warn("Claim boundary min Y is greater than max Y, correcting...");
            int temp = this.claimBoundaryMinY;
            this.claimBoundaryMinY = this.claimBoundaryMaxY;
            this.claimBoundaryMaxY = temp;
        }

        this.claimBoundaryFillOpacity = clampInt(this.claimBoundaryFillOpacity, 0, 100, "Invalid claim boundary fill opacity, correcting...");
        this.claimBoundaryOutlineOpacity = clampInt(this.claimBoundaryOutlineOpacity, 0, 100, "Invalid claim boundary outline opacity, correcting...");

        if (this.overlayHorizontalAlignment == null) {
            ClaimVisualizerMod.LOGGER.warn("Invalid overlay horizontal alignment, correcting...");
            this.overlayHorizontalAlignment = DefaultConfig.DEFAULT_OVERLAY_HORIZONTAL_ALIGNMENT;
        }

        if (this.overlayVerticalAlignment == null) {
            ClaimVisualizerMod.LOGGER.warn("Invalid overlay vertical alignment, correcting...");
            this.overlayVerticalAlignment = DefaultConfig.DEFAULT_OVERLAY_VERTICAL_ALIGNMENT;
        }

        this.apiRequestTimeoutMillis = clampLong(this.apiRequestTimeoutMillis, 100, 60000, "Invalid API request timeout, correcting...");

        boolean uriValid = true;

        try {
            URI uri = URI.create(this.customApiEndpointUri);
            if ((!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null) uriValid = false;
        } catch (Exception e) {
            uriValid = false;
        }

        if (!uriValid) {
            ClaimVisualizerMod.LOGGER.warn("Invalid custom API endpoint URI, correcting...");
            this.overrideApiEndpoint = false;
            this.customApiEndpointUri = "https://example.com";
        }

        return InteractionResult.SUCCESS;
    }

    private int clampInt(int value, int min, int max, String warningMessage) {
        return (int) clampLong(value, min, max, warningMessage);
    }

    private long clampLong(long value, long min, long max, String warningMessage) {
        if (value < min || value > max) {
            ClaimVisualizerMod.LOGGER.warn(warningMessage);
            return Math.clamp(value, min, max);
        }

        return value;
    }

    @Override
    public boolean isClaimBoundaryCorrectionEnabled() {
        return this.claimBoundaryCorrectionEnabled;
    }

    @Override
    public int getClaimBoundaryMinY() {
        return this.claimBoundaryMinY;
    }

    @Override
    public int getClaimBoundaryMaxY() {
        return this.claimBoundaryMaxY;
    }

    @Override
    public Vector3f getDefaultClaimBoundaryRgb() {
        return Color.parseArgbInt(this.defaultClaimBoundaryColor).xyz(new Vector3f());
    }

    @Override
    public float getClaimBoundaryFillOpacity() {
        return this.claimBoundaryFillOpacity / 100f;
    }

    @Override
    public float getClaimBoundaryOutlineOpacity() {
        return this.claimBoundaryOutlineOpacity / 100f;
    }

    @Override
    public boolean isDefaultBoundaryColorForced() {
        return this.defaultClaimBoundaryColorForced;
    }

    @Override
    public int getOverlayX() {
        return this.overlayX;
    }

    @Override
    public int getOverlayY() {
        return this.overlayY;
    }

    @Override
    public Alignment getOverlayHorizontalAlignment() {
        return this.overlayHorizontalAlignment;
    }

    @Override
    public Alignment getOverlayVerticalAlignment() {
        return this.overlayVerticalAlignment;
    }

    @Override
    public Duration getApiRequestTimeout() {
        return Duration.ofMillis(this.apiRequestTimeoutMillis);
    }

    @Override
    public URI getApiEndpointUri() {
        return this.overrideApiEndpoint ? URI.create(this.customApiEndpointUri) : DefaultConfig.DEFAULT_API_ENDPOINT_URI;
    }

}
