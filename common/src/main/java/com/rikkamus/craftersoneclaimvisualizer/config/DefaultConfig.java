package com.rikkamus.craftersoneclaimvisualizer.config;

import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;

import java.net.URI;
import java.time.Duration;

public class DefaultConfig implements ClaimVisualizerConfig {

    public static final int DEFAULT_OVERLAY_X = 5;
    public static final int DEFAULT_OVERLAY_Y = 5;
    public static final Alignment DEFAULT_OVERLAY_HORIZONTAL_ALIGNMENT = Alignment.START;
    public static final Alignment DEFAULT_OVERLAY_VERTICAL_ALIGNMENT = Alignment.START;
    public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(5);
    public static final URI DEFAULT_API_ENDPOINT_URI = URI.create("https://figbash.com/claims/data/claims.json");

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
