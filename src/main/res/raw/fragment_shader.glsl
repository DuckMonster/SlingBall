precision mediump float;

uniform vec4 u_color;
uniform vec3 u_alphaColor;
uniform vec3 u_bloom;
uniform sampler2D u_texture;

varying vec2 v_texturePosition;

void main() {
	vec4 tex = texture2D(u_texture, v_texturePosition);

	if (tex.a == 0.0 || tex.rgb == u_alphaColor) {
		discard;
	} else {
		vec4 glColor = u_color * tex;
		gl_FragColor = glColor;
	}
}