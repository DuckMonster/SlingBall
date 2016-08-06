precision mediump float;

uniform vec4 u_color;
uniform vec3 u_alphaColor;
uniform sampler2D u_texture;

varying vec2 v_texturePosition;

uniform vec2 u_tilePosition;
uniform vec2 u_tileSize;
uniform vec2 u_tileSpacing;
uniform vec3 u_tileSpaceColor;

void main() {
	vec2 tilePos = v_texturePosition + (u_tileSize + u_tileSpacing) * u_tilePosition;

	vec4 color = texture2D(u_texture, tilePos);
	if (color.a == 0.0 || color.rgb == u_alphaColor || color.rgb == u_tileSpaceColor) {
		discard;
	} else {
		gl_FragColor = u_color * color;
	}
}