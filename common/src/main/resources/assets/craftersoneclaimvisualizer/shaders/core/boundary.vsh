#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;
out float sphericalVertexDistance;
out float cylindricalVertexDistance;

void main() {
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewPos;

    vertexColor = Color;
    sphericalVertexDistance = fog_spherical_distance(vec3(viewPos));
    cylindricalVertexDistance = fog_cylindrical_distance(vec3(viewPos));
}
