package com.rikkamus.craftersoneclaimvisualizer;

import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import com.rikkamus.craftersoneclaimvisualizer.render.BoundaryRenderer;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor
public class ClaimManager {

    @Getter
    private final List<Claim> claims = new ArrayList<>();

    public void renderClaimBoundaries(RenderContext context, float y1, float y2, Function<Claim, Vector3f> rgbSupplier, float fillOpacity, float outlineOpacity, boolean useCorrectedShape) {
        BoundaryRenderer.renderBoundaries(context, this.claims.stream().map(claim -> {
            Vector3f claimRgb = rgbSupplier.apply(claim);
            Vector4f fillRgba = new Vector4f(claimRgb.x, claimRgb.y, claimRgb.z, fillOpacity);
            Vector4f outlineRgba = new Vector4f(claimRgb.x, claimRgb.y, claimRgb.z, outlineOpacity);

            Polygon shape = useCorrectedShape ? claim.getCorrectedShape() : claim.getRawShape();
            return new Boundary(shape, y1, y2, fillRgba, outlineRgba);
        }).toList());
    }

    public Optional<Claim> getClaimAt(double x, double z, boolean useCorrectedShape) {
        for (Claim claim : this.claims) {
            Polygon shape = useCorrectedShape ? claim.getCorrectedShape() : claim.getRawShape();
            if (shape.contains(x, z)) return Optional.of(claim);
        }

        return Optional.empty();
    }

    public void addClaim(Claim claim) {
        this.claims.add(claim);
    }

    public void addAllClaims(Iterable<Claim> claims) {
        claims.forEach(this::addClaim);
    }

    public void clearClaims() {
        this.claims.clear();
    }

}
