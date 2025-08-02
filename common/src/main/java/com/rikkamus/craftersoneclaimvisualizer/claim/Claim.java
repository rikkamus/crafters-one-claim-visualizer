package com.rikkamus.craftersoneclaimvisualizer.claim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rikkamus.craftersoneclaimvisualizer.ChatLogger;
import com.rikkamus.craftersoneclaimvisualizer.ClaimVisualizerMod;
import com.rikkamus.craftersoneclaimvisualizer.Color;
import com.rikkamus.craftersoneclaimvisualizer.geometry.PolygonUtil;
import lombok.*;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Claim {

    @JsonProperty("claimID")
    private String claimId;

    private String owner;
    private List<String> collaborators;
    private String type;
    private String description;

    @JsonIgnore
    private Vector3f rgb;

    @NotNull
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private Polygon rawShape;

    @NotNull
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private Polygon correctedShape;

    @JsonCreator
    public Claim(@JsonProperty("claimID") String claimId,
                 @JsonProperty("owner") String owner,
                 @JsonProperty("collaborators") List<String> collaborators,
                 @JsonProperty("type") String type,
                 @JsonProperty("description") String description,
                 @JsonProperty("color") String color,
                 @JsonProperty(value = "coords", required = true) int[][] rawCoords) {
        this.claimId = claimId;
        this.owner = owner;
        this.collaborators = collaborators;
        this.type = type;
        this.description = description;
        setColor(color);
        setRawCoords(rawCoords);
    }

    @JsonProperty("color")
    public String getColor() {
        return Color.toRgbaHex(new Vector4f(this.rgb.x, this.rgb.y, this.rgb.z, 1.0f));
    }

    public void setColor(String color) {
        this.rgb = color != null ? Color.parseRgbaHex(color).xyz(new Vector3f()) : null;
    }

    @JsonProperty(value = "coords")
    public int[][] getRawCoords() {
        int[][] rawCoords = new int[this.rawShape.npoints][2];

        for (int i = 0; i < this.rawShape.npoints; i++) {
            rawCoords[i][0] = this.rawShape.xpoints[i];
            rawCoords[i][1] = this.rawShape.ypoints[i];
        }

        return rawCoords;
    }

    public void setRawCoords(int[][] rawCoords) {
        Objects.requireNonNull(rawCoords);
        List<Vector2i> vectors = new ArrayList<>(rawCoords.length);

        for (int i = 0; i < rawCoords.length; i++) {
            if (rawCoords[i].length != 2) throw new IllegalArgumentException("Polygon points require exactly two coordinates.");
            vectors.add(new Vector2i(rawCoords[i][0], rawCoords[i][1]));
        }

        this.rawShape = PolygonUtil.createPolygonFromRawPoints(vectors);

        try {
            this.correctedShape = PolygonUtil.createPolygonFromBlockPoints(vectors);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to create corrected claim shape for claim with ID \"%s\". Uncorrected shape will be used instead.", this.claimId);
            ClaimVisualizerMod.LOGGER.error(errorMessage, e);
            ChatLogger.log(errorMessage, ChatFormatting.RED);

            this.correctedShape = this.rawShape;
        }
    }

}
