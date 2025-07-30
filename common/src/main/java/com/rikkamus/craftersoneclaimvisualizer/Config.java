package com.rikkamus.craftersoneclaimvisualizer;

import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;

import java.time.Duration;

public interface Config {

    Duration getApiRequestTimeout();

    int getOverlayX();

    int getOverlayY();

    Alignment getOverlayHorizontalAlignment();

    Alignment getOverlayVerticalAlignment();

}
