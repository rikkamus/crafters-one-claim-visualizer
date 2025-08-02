package com.rikkamus.craftersoneclaimvisualizer.config;

import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import org.joml.Vector3f;

import java.net.URI;
import java.time.Duration;

public interface ClaimVisualizerConfig {

    boolean isClaimBoundaryCorrectionEnabled();

    int getClaimBoundaryMinY();

    int getClaimBoundaryMaxY();

    Vector3f getDefaultClaimBoundaryRgb();

    float getClaimBoundaryFillOpacity();

    float getClaimBoundaryOutlineOpacity();

    boolean isDefaultBoundaryColorForced();

    int getOverlayX();

    int getOverlayY();

    Alignment getOverlayHorizontalAlignment();

    Alignment getOverlayVerticalAlignment();

    Duration getApiRequestTimeout();

    URI getApiEndpointUri();

}
