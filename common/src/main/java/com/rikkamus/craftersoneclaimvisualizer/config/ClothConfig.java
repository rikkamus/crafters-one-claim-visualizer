package com.rikkamus.craftersoneclaimvisualizer.config;

import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.net.URI;
import java.time.Duration;

@Config(name = ClaimVisualizerMod.MOD_ID)
public class ClothConfig implements ClaimVisualizerConfig, ConfigData {

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
