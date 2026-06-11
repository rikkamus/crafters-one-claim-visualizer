#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

layout(std140) uniform BoundaryUniforms {
    float boundaryMinY;
    float boundaryMaxY;
    vec4 boundaryColor;
    int boundaryFogEnabled;
};

void main() {
    if (boundaryColor.a == 0.0) {
        discard;
    }

    if (boundaryFogEnabled != 0) {
        fragColor = apply_fog(boundaryColor * ColorModulator, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
    } else {
        fragColor = boundaryColor * ColorModulator;
    }
}
