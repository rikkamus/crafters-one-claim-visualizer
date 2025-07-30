package com.rikkamus.craftersoneclaimvisualizer.config;

import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;

import java.time.Duration;

public interface ClaimVisualizerConfig {

    int getOverlayX();

    int getOverlayY();

    Alignment getOverlayHorizontalAlignment();

    Alignment getOverlayVerticalAlignment();

    Duration getApiRequestTimeout();

}
