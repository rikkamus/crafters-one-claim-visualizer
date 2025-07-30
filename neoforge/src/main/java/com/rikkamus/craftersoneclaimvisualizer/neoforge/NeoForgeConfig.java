package com.rikkamus.craftersoneclaimvisualizer.neoforge;

import com.rikkamus.craftersoneclaimvisualizer.Config;
import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class NeoForgeConfig implements Config {

    private static final NeoForgeConfig INSTANCE;
    private static final ModConfigSpec SPEC;

    static {
        Pair<NeoForgeConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(NeoForgeConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public static NeoForgeConfig getInstance() {
        return INSTANCE;
    }

    public static ModConfigSpec getSpec() {
        return SPEC;
    }

    private final ModConfigSpec.LongValue apiRequestTimeoutMillis;
    private final ModConfigSpec.ConfigValue<Integer> overlayX;
    private final ModConfigSpec.ConfigValue<Integer> overlayY;
    private final ModConfigSpec.EnumValue<Alignment> overlayHorizontalAlignment;
    private final ModConfigSpec.EnumValue<Alignment> overlayVerticalAlignment;

    private NeoForgeConfig(ModConfigSpec.Builder builder) {
        builder.push("api");
        this.apiRequestTimeoutMillis = builder.gameRestart()
                                              .defineInRange("request_timeout_millis", 5000L, 100L, 60000L);
        builder.pop();

        builder.push("overlay");
        this.overlayX = builder.comment("Positive values define distance from the left edge of the screen")
                               .comment("Negative values define distance from the right edge of the screen")
                               .define("x", 5);
        this.overlayY = builder.comment("Positive values define distance from the top edge of the screen")
                               .comment("Negative values define distance from the bottom edge of the screen")
                               .define("y", 5);
        this.overlayHorizontalAlignment = builder.defineEnum("horizontal_alignment", Alignment.START);
        this.overlayVerticalAlignment = builder.defineEnum("vertical_alignment", Alignment.START);
        builder.pop();
    }

    @Override
    public Duration getApiRequestTimeout() {
        return Duration.of(this.apiRequestTimeoutMillis.get(), ChronoUnit.MILLIS);
    }

    @Override
    public int getOverlayX() {
        return this.overlayX.get();
    }

    @Override
    public int getOverlayY() {
        return this.overlayY.get();
    }

    @Override
    public Alignment getOverlayHorizontalAlignment() {
        return this.overlayHorizontalAlignment.get();
    }

    @Override
    public Alignment getOverlayVerticalAlignment() {
        return this.overlayVerticalAlignment.get();
    }

}
