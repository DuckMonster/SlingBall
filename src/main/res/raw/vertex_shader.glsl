uniform mat4 u_mvpMatrix;
attribute vec3 a_vertexPosition;
attribute vec2 a_texturePosition;

varying vec2 v_texturePosition;

void main() {
	v_texturePosition = a_texturePosition;
	gl_Position = u_mvpMatrix * vec4(a_vertexPosition, 1.0);
}