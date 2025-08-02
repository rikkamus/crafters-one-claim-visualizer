package com.rikkamus.craftersoneclaimvisualizer.config;

import com.rikkamus.craftersoneclaimvisualizer.Color;
import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import org.joml.Vector3f;

import java.net.URI;
import java.time.Duration;

public class DefaultConfig implements ClaimVisualizerConfig {

    public static final boolean DEFAULT_BOUNDARY_CORRECTION_ENABLED = true;
    public static final int DEFAULT_BOUNDARY_MIN_Y = -64;
    public static final int DEFAULT_BOUNDARY_MAX_Y = 100;
    public static final int DEFAULT_BOUNDARY_COLOR = 0x0015E685;
    public static final boolean DEFAULT_FORCE_DEFAULT_BOUNDARY_COLOR = false;
    public static final float DEFAULT_BOUNDARY_FILL_OPACITY = 0.2f;
    public static final float DEFAULT_BOUNDARY_OUTLINE_OPACITY = 0.8f;
    public static final int DEFAULT_OVERLAY_X = 5;
    public static final int DEFAULT_OVERLAY_Y = 5;
    public static final Alignment DEFAULT_OVERLAY_HORIZONTAL_ALIGNMENT = Alignment.START;
    public static final Alignment DEFAULT_OVERLAY_VERTICAL_ALIGNMENT = Alignment.START;
    public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(5);
    public static final URI DEFAULT_API_ENDPOINT_URI = URI.create("https://figbash.com/claims/data/claims.json");

    @Override
    public boolean isClaimBoundaryCorrectionEnabled() {
        return DefaultConfig.DEFAULT_BOUNDARY_CORRECTION_ENABLED;
    }

    @Override
    public int getClaimBoundaryMinY() {
        return DefaultConfig.DEFAULT_BOUNDARY_MIN_Y;
    }

    @Override
    public int getClaimBoundaryMaxY() {
        return DefaultConfig.DEFAULT_BOUNDARY_MAX_Y;
    }

    @Override
    public Vector3f getDefaultClaimBoundaryRgb() {
        return Color.parseArgbInt(DefaultConfig.DEFAULT_BOUNDARY_COLOR).xyz(new Vector3f());
    }

    @Override
    public float getClaimBoundaryFillOpacity() {
        return DefaultConfig.DEFAULT_BOUNDARY_FILL_OPACITY;
    }

    @Override
    public float getClaimBoundaryOutlineOpacity() {
        return DefaultConfig.DEFAULT_BOUNDARY_OUTLINE_OPACITY;
    }

    @Override
    public boolean isDefaultBoundaryColorForced() {
        return DefaultConfig.DEFAULT_FORCE_DEFAULT_BOUNDARY_COLOR;
    }

    @Override
    public int getOverlayX() {
        return DefaultConfig.DEFAULT_OVERLAY_X;
    }

    @Override
    public int getOverlayY() {
        return DefaultConfig.DEFAULT_OVERLAY_Y;
    }

    @Override
    public Alignment getOverlayHorizontalAlignment() {
        return DefaultConfig.DEFAULT_OVERLAY_HORIZONTAL_ALIGNMENT;
    }

    @Override
    public Alignment getOverlayVerticalAlignment() {
        return DefaultConfig.DEFAULT_OVERLAY_VERTICAL_ALIGNMENT;
    }

    @Override
    public Duration getApiRequestTimeout() {
        return DefaultConfig.DEFAULT_REQUEST_TIMEOUT;
    }

    @Override
    public URI getApiEndpointUri() {
        return DefaultConfig.DEFAULT_API_ENDPOINT_URI;
    }

}
