package com.rikkamus.craftersoneclaimvisualizer.claim;

import com.rikkamus.craftersoneclaimvisualizer.ChatLogger;
import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.Color;
import com.rikkamus.craftersoneclaimvisualizer.geometry.PolygonUtil;
import net.minecraft.ChatFormatting;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ClaimRepository implements AutoCloseable {

    private static boolean isStatusOk(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }

    private static Claim parseClaim(JSONObject jsonClaim) {
        String claimId = jsonClaim.has("claimID") ? jsonClaim.getString("claimID") : null;
        String owner = jsonClaim.has("owner") ? jsonClaim.getString("owner") : null;
        String type = jsonClaim.has("type") ? jsonClaim.getString("type") : null;
        Vector3f rgb = jsonClaim.has("color") ? Color.parseRgbaHex(jsonClaim.getString("color")).xyz(new Vector3f()) : null;

        JSONArray jsonPoints = jsonClaim.getJSONArray("coords");
        List<Vector2i> points = new ArrayList<>(jsonPoints.length());

        for (int i = 0; i < jsonPoints.length(); i++) {
            JSONArray jsonPoint = jsonPoints.getJSONArray(i);
            if (jsonPoint.length() != 2) throw new IllegalArgumentException("Invalid claim points.");

            points.add(new Vector2i(jsonPoint.getInt(0), jsonPoint.getInt(1)));
        }

        Polygon rawShape = PolygonUtil.createPolygonFromRawPoints(points);
        Polygon correctedShape;

        try {
            correctedShape = PolygonUtil.createPolygonFromBlockPoints(points);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to create corrected claim shape for claim with ID \"%s\". Uncorrected shape will be used instead.", claimId);
            ClaimVisualizerMod.LOGGER.error(errorMessage, e);
            ChatLogger.log(errorMessage, ChatFormatting.RED);

            correctedShape = rawShape;
        }

        return new Claim(claimId, owner, type, rgb, rawShape, correctedShape);
    }

    private final URI endpointUri;
    private final String userAgent;
    private final Duration timeout;
    private final HttpClient client;

    public ClaimRepository(URI endpointUri, String userAgent, Duration timeout) {
        this.endpointUri = endpointUri;
        this.userAgent = userAgent;
        this.timeout = timeout;
        this.client = HttpClient.newBuilder()
                                .connectTimeout(timeout)
                                .followRedirects(HttpClient.Redirect.NORMAL)
                                .build();
    }

    public CompletableFuture<Collection<Claim>> findAllClaims() {
        HttpRequest request = HttpRequest.newBuilder(this.endpointUri)
                                         .setHeader("Accept", "application/json")
                                         .setHeader("User-Agent", this.userAgent)
                                         .timeout(this.timeout)
                                         .build();

        return this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).exceptionally(e -> {
            if (e instanceof HttpTimeoutException || (e instanceof CompletionException && e.getCause() instanceof HttpTimeoutException)) {
                throw new RepositoryFetchException("Request timed out.", e);
            } else {
                throw new RuntimeException(e);
            }
        }).thenApply(response -> {
            int status = response.statusCode();
            if (!ClaimRepository.isStatusOk(status)) {
                throw new RepositoryFetchException(String.format("Received error response from server (response code: %d).", status));
            }

            try {
                JSONArray jsonClaims = new JSONArray(response.body());
                List<Claim> claims = new ArrayList<>(jsonClaims.length());

                for (int i = 0; i < jsonClaims.length(); i++) {
                    claims.add(ClaimRepository.parseClaim(jsonClaims.getJSONObject(i)));
                }

                return claims;
            } catch (Exception e) {
                throw new RepositoryFetchException("Failed to parse response body.", e);
            }
        });
    }

    @Override
    public void close() {
        this.client.close();
    }

}
