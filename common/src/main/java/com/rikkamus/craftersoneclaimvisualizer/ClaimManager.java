package com.rikkamus.craftersoneclaimvisualizer;

import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import com.rikkamus.craftersoneclaimvisualizer.render.BoundaryRenderer;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import lombok.NoArgsConstructor;
import org.joml.Vector3fc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class ClaimManager implements AutoCloseable {

    private final List<Claim> claims = new ArrayList<>();

    private final BoundaryRenderer boundaryRenderer = new BoundaryRenderer();

    public void renderClaimBoundaries(
        RenderContext context,
        float boundaryMinY,
        float boundaryMaxY,
        Vector3fc defaultRgb,
        float fillOpacity,
        float outlineOpacity,
        boolean useCorrectedBoundaries,
        boolean forceDefaultColor,
        boolean applyFog
    ) {
        this.boundaryRenderer.render(context, boundaryMinY, boundaryMaxY, defaultRgb, fillOpacity, outlineOpacity, useCorrectedBoundaries, forceDefaultColor, applyFog);
    }

    public Optional<Claim> getClaimAt(double x, double z, boolean useCorrectedShape) {
        for (Claim claim : this.claims) {
            Polygon shape = useCorrectedShape ? claim.getCorrectedShape() : claim.getRawShape();
            if (shape.contains(x, z)) return Optional.of(claim);
        }

        return Optional.empty();
    }

    public void setClaims(Collection<Claim> claims) {
        this.claims.clear();
        this.claims.addAll(claims);

        this.boundaryRenderer.setClaims(claims);
    }

    @Override
    public void close() throws Exception {
        this.boundaryRenderer.close();
    }

}
