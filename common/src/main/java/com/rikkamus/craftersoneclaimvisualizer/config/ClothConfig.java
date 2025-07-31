package com.rikkamus.craftersoneclaimvisualizer.config;

import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.Color;
import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import org.joml.Vector3f;

import java.net.URI;
import java.time.Duration;

@Config(name = ClaimVisualizerMod.MOD_ID)
public class ClothConfig implements ClaimVisualizerConfig, ConfigData {

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
        if (this.claimBoundaryMinY > this.claimBoundaryMaxY) {
            int temp = this.claimBoundaryMinY;
            this.claimBoundaryMinY = this.claimBoundaryMaxY;
            this.claimBoundaryMaxY = temp;
        }

        this.claimBoundaryFillOpacity = Math.clamp(this.claimBoundaryFillOpacity, 0, 100);
        this.claimBoundaryOutlineOpacity = Math.clamp(this.claimBoundaryOutlineOpacity, 0, 100);

        if (this.overlayHorizontalAlignment == null) this.overlayHorizontalAlignment = DefaultConfig.DEFAULT_OVERLAY_HORIZONTAL_ALIGNMENT;
        if (this.overlayVerticalAlignment == null) this.overlayVerticalAlignment = DefaultConfig.DEFAULT_OVERLAY_VERTICAL_ALIGNMENT;
        this.apiRequestTimeoutMillis = Math.clamp(this.apiRequestTimeoutMillis, 100, 60000);

        boolean uriValid = true;

        try {
            URI uri = URI.create(this.customApiEndpointUri);
            if ((!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null) uriValid = false;
        } catch (Exception e) {
            uriValid = false;
        }

        if (!uriValid) {
            this.overrideApiEndpoint = false;
            this.customApiEndpointUri = "https://example.com";
        }
    }

    @Override
    public int getClaimBoundaryMinY() {
        return Math.min(this.claimBoundaryMinY, this.claimBoundaryMaxY);
    }

    @Override
    public int getClaimBoundaryMaxY() {
        return Math.max(this.claimBoundaryMinY, this.claimBoundaryMaxY);
    }

    @Override
    public Vector3f getDefaultClaimBoundaryRgb() {
        return Color.parseArgbInt(this.defaultClaimBoundaryColor).xyz(new Vector3f());
    }

    @Override
    public float getClaimBoundaryFillOpacity() {
        return Math.clamp(this.claimBoundaryFillOpacity, 0, 100) / 100f;
    }

    @Override
    public float getClaimBoundaryOutlineOpacity() {
        return Math.clamp(this.claimBoundaryOutlineOpacity, 0, 100) / 100f;
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
