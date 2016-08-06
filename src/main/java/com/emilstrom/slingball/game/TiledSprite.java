package com.emilstrom.slingball.game;

import android.opengl.GLES20;

import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.ShaderHelper;
import com.emilstrom.slingball.helper.TextureHelper;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-22.
 */
public class TiledSprite extends Sprite {
	private int u_tilePosition, u_tileSize, u_tileSpacing, u_tileSpaceColor;
	public Vertex tileSize, tileSpace;

	Color tileSpaceColor = new Color();

	public TiledSprite() {
		super(R.drawable.digits);
		setTileSpaceColor(0f, 0f, 0f);
	}

	@Override
	public void init(int textureID) {
		shaderProgram = ShaderHelper.shaderProgramTile;

		tileSize = new Vertex(7f, 10f);

		int texSize[] = new int[2];
		textureHandler = TextureHelper.loadTexture(textureID, texSize);

		tileSize = new Vertex(tileSize.x / (float)texSize[0], tileSize.y / (float)texSize[1]);
		tileSpace = new Vertex(1f / (float)texSize[0], 1f / (float)texSize[1]);

		final float vertexData[] = {
				-0.5f, -0.5f, 0f,		0f, (float) tileSize.y,
				-0.5f, 0.5f, 0f,		0f, 0,
				0.5f, -0.5f, 0f,		(float) tileSize.x, (float) tileSize.y,
				0.5f, 0.5f, 0f,			(float) tileSize.x, 0
		};

		setBuffer(vertexData);

		reset();
		setColor(1f, 1f, 1f, 1f);
		setAlphaColor(1f, 0f, 1f);
	}

	@Override
	public void bindAttributes() {
		super.bindAttributes();

		u_tilePosition = GLES20.glGetUniformLocation(shaderProgram, "u_tilePosition");
		u_tileSize = GLES20.glGetUniformLocation(shaderProgram, "u_tileSize");
		u_tileSpacing = GLES20.glGetUniformLocation(shaderProgram, "u_tileSpacing");
		u_tileSpaceColor = GLES20.glGetUniformLocation(shaderProgram, "u_tileSpaceColor");
	}

	public void setTileSpaceColor(Color c) { tileSpaceColor.copy(c); }
	public void setTileSpaceColor(double r, double g, double b) {
		tileSpaceColor.copy(r, g, b, 1.0);
	}

	@Override
	public void uploadData() {
		super.uploadData();
		tileSpaceColor.confine();

		GLES20.glUniform3f(u_tileSpaceColor, (float)tileSpaceColor.r, (float)tileSpaceColor.g, (float)tileSpaceColor.b);
	}

	public void draw(int tilex, int tiley) {
		//Set tile uniforms
		GLES20.glUniform2f(u_tilePosition, tilex, tiley);
		GLES20.glUniform2f(u_tileSize, (float) tileSize.x, (float) tileSize.y);
		GLES20.glUniform2f(u_tileSpacing, (float) tileSpace.x, (float) tileSpace.y);

		super.draw();
	}
}
