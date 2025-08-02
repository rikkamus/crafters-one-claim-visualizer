package com.rikkamus.craftersoneclaimvisualizer;

import lombok.*;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PolygonalClaim {

    private String claimId;
    private String owner;
    private List<String> collaborators;
    private String type;
    private String description;
    private Vector3f rgb;
    private Polygon shape;

}
