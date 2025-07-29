package com.rikkamus.craftersoneclaimvisualizer.claim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rikkamus.craftersoneclaimvisualizer.convert.IntArrayToVector2iConverter;
import com.rikkamus.craftersoneclaimvisualizer.convert.Vector2iToIntArrayConverter;
import lombok.*;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Claim {

    @JsonProperty("claimID")
    private String claimId;

    private String owner;
    private List<String> collaborators;
    private String type;
    private String description;
    private String color = "#15E685";

    @JsonSerialize(contentConverter = Vector2iToIntArrayConverter.class)
    @JsonDeserialize(contentConverter = IntArrayToVector2iConverter.class)
    private List<Vector2i> coords = new ArrayList<>();

}
