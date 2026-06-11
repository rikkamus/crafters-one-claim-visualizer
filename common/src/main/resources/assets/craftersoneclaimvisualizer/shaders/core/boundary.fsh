#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in vec4 vertexColor;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

layout(std140) uniform BoundaryUniforms {
    int boundaryFogEnabled;
};

void main() {
    if (vertexColor.a == 0.0) {
        discard;
    }

    if (boundaryFogEnabled != 0) {
        fragColor = apply_fog(vertexColor * ColorModulator, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
    } else {
        fragColor = vertexColor * ColorModulator;
    }
}
