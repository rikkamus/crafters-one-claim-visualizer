package com.rikkamus.craftersoneclaimvisualizer.claim;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public class Claim {

    private String claimId;
    private String owner;
    private String type;
    private Vector3f rgb;

    @NotNull
    @Setter(AccessLevel.NONE)
    private Polygon rawShape;

    @NotNull
    @Setter(AccessLevel.NONE)
    private Polygon correctedShape;

}
