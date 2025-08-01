package com.rikkamus.craftersoneclaimvisualizer.claim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rikkamus.craftersoneclaimvisualizer.Color;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
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
    private Polygon shape;

    @JsonCreator
    public Claim(@JsonProperty("claimID") String claimId,
                 @JsonProperty("owner") String owner,
                 @JsonProperty("collaborators") List<String> collaborators,
                 @JsonProperty("type") String type,
                 @JsonProperty("description") String description,
                 @JsonProperty("color") String color,
                 @JsonProperty(value = "coords", required = true) int[][] points) {
        this.claimId = claimId;
        this.owner = owner;
        this.collaborators = collaborators;
        this.type = type;
        this.description = description;
        setColor(color);
        setCoords(Objects.requireNonNull(points));
    }

    @JsonProperty("color")
    public String getColor() {
        return Color.toRgbaHex(new Vector4f(this.rgb.x, this.rgb.y, this.rgb.z, 1.0f));
    }

    @JsonProperty("color")
    public void setColor(String color) {
        this.rgb = color != null ? Color.parseRgbaHex(color).xyz(new Vector3f()) : null;
    }

    @JsonProperty("coords")
    public int[][] getCoords() {
        int[][] points = new int[this.shape.npoints][2];

        for (int i = 0; i < this.shape.npoints; i++) {
            points[i][0] = this.shape.xpoints[i];
            points[i][1] = this.shape.ypoints[i];
        }

        return points;
    }

    @JsonProperty(value = "coords", required = true)
    public void setCoords(int[][] points) {
        Objects.requireNonNull(points);
        int[] xpoints = new int[points.length];
        int[] ypoints = new int[points.length];

        for (int i = 0; i < points.length; i++) {
            if (points[i].length != 2) throw new IllegalArgumentException("Polygon points require exactly two coordinates.");
            xpoints[i] = points[i][0];
            ypoints[i] = points[i][1];
        }

        this.shape = new Polygon(xpoints, ypoints, points.length);
    }

}
