package com.rikkamus.craftersoneclaimvisualizer;

import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import com.rikkamus.craftersoneclaimvisualizer.render.BoundaryRenderer;
import com.rikkamus.craftersoneclaimvisualizer.render.RenderContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class ClaimManager {

    private static final float BOUNDARY_MIN_Y = -64f;
    private static final float BOUNDARY_MAX_Y = 320f;
    private static final float BOUNDARY_FILL_OPACITY = 0.2f;
    private static final float BOUNDARY_OUTLINE_OPACITY = 0.8f;

    @Getter
    private final List<Claim> claims = new ArrayList<>();

    private final List<Boundary> boundaries = new ArrayList<>();

    public void renderClaimBoundaries(RenderContext context) {
        BoundaryRenderer.renderBoundaries(context, this.boundaries);
    }

    public void addClaim(Claim claim) {
        this.claims.add(claim);

        Vector4f fillColor = HexColor.parse(claim.getColor());
        Vector4f outlineColor = new Vector4f(fillColor);
        fillColor.w = ClaimManager.BOUNDARY_FILL_OPACITY;
        outlineColor.w = ClaimManager.BOUNDARY_OUTLINE_OPACITY;

        this.boundaries.add(new Boundary(
            claim.getShape(),
            ClaimManager.BOUNDARY_MIN_Y,
            ClaimManager.BOUNDARY_MAX_Y,
            fillColor,
            outlineColor
        ));
    }

    public void addAllClaims(Iterable<Claim> claims) {
        claims.forEach(this::addClaim);
    }

    public Optional<Claim> getClaimAt(double x, double z) {
        for (Claim claim : this.claims) {
            if (claim.getShape().contains(x, z)) return Optional.of(claim);
        }

        return Optional.empty();
    }

    public void clearClaims() {
        this.claims.clear();
        this.boundaries.clear();
    }

}
