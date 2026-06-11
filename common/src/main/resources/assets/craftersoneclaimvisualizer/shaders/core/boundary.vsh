#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;

layout(std140) uniform BoundaryUniforms {
    float boundaryMinY;
    float boundaryMaxY;
    vec4 boundaryColor;
    int boundaryFogEnabled;
};

void main() {
    vec3 worldPosition = vec3(Position.x, mix(boundaryMinY, boundaryMaxY, Position.y), Position.z);
    vec4 viewPos = ModelViewMat * vec4(worldPosition, 1.0);
    gl_Position = ProjMat * viewPos;

    sphericalVertexDistance = fog_spherical_distance(vec3(viewPos));
    cylindricalVertexDistance = fog_cylindrical_distance(vec3(viewPos));
}
