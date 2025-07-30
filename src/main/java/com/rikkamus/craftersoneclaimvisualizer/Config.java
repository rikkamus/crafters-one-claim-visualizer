package com.rikkamus.craftersoneclaimvisualizer;

import com.rikkamus.craftersoneclaimvisualizer.render.Alignment;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Config {

    private static final Config INSTANCE;
    private static final ModConfigSpec SPEC;

    static {
        Pair<Config, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Config::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public static Config getInstance() {
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

    private Config(ModConfigSpec.Builder builder) {
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

    public Duration getApiRequestTimeout() {
        return Duration.of(this.apiRequestTimeoutMillis.get(), ChronoUnit.MILLIS);
    }

    public int getOverlayX() {
        return this.overlayX.get();
    }

    public int getOverlayY() {
        return this.overlayY.get();
    }

    public Alignment getOverlayHorizontalAlignment() {
        return this.overlayHorizontalAlignment.get();
    }

    public Alignment getOverlayVerticalAlignment() {
        return this.overlayVerticalAlignment.get();
    }

}
