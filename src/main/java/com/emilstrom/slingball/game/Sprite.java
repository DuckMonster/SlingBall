package com.emilstrom.slingball.game;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.ShaderHelper;
import com.emilstrom.slingball.helper.TextureHelper;
import com.emilstrom.slingball.helper.Vertex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Emil on 2014-02-19.
 */
public class Sprite {
	protected int vertexBufferObject,
			textureHandler, shaderProgram;

	protected int a_position, a_texturePosition, u_color, u_mvp,
			u_texture, u_alphaColor;

	protected FloatBuffer vertexBuffer;

	protected float[] modelMatrix = new float[16];

	private Color spriteColor=new Color(), alphaColor=new Color();
	public Vertex textureSize;
	private int fileTextureSize[];

	public Sprite(int textureID) {
		init(textureID);
	}
	public Sprite(int textureID, Vertex textureSize) {
		this.textureSize = textureSize;
		init(textureID);
	}

	public void init(int textureID) {
		setTexture(textureID);

		final float vertexData[];

		if (textureSize != null) {
			float w = (float)textureSize.y / (float)fileTextureSize[0],
					h = (float)textureSize.x / (float)fileTextureSize[1];

			vertexData = new float[] {
					-0.5f, -0.5f, 0f,		0f, h,
					-0.5f, 0.5f, 0f,		0f, 0f,
					0.5f, -0.5f, 0f,		w, h,
					0.5f, 0.5f, 0f,			h, 0f
			};
		} else {
			vertexData = new float[] {
					-0.5f, -0.5f, 0f,		0f, 1f,
					-0.5f, 0.5f, 0f,		0f, 0f,
					0.5f, -0.5f, 0f,		1f, 1f,
					0.5f, 0.5f, 0f,			1f, 0f
			};
		}

		shaderProgram = ShaderHelper.shaderProgram2D;
		setBuffer(vertexData);

		reset();
		setColor(1f, 1f, 1f, 1f);
		setAlphaColor(1f, 0f, 1f);
	}

	public void setTexture(int resourceID) {
		int size[] = new int[2];
		textureHandler = TextureHelper.loadTexture(resourceID, size);

		fileTextureSize = size;
	}

	public void setBuffer(float vertexData[]) {
		vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(vertexData);
		vertexBuffer.position(0);

		genBufferObject();
	}

	public void genBufferObject() {
		int buffers[] = new int[1];
		GLES20.glGenBuffers(1, buffers, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);

		vertexBufferObject = buffers[0];

		bindAttributes();
	}

	public void bindAttributes() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObject);

		a_position = GLES20.glGetAttribLocation(shaderProgram, "a_vertexPosition");
		a_texturePosition = GLES20.glGetAttribLocation(shaderProgram, "a_texturePosition");

		u_mvp = GLES20.glGetUniformLocation(shaderProgram, "u_mvpMatrix");
		u_color = GLES20.glGetUniformLocation(shaderProgram, "u_color");
		u_texture = GLES20.glGetUniformLocation(shaderProgram, "u_texture");
		u_alphaColor = GLES20.glGetUniformLocation(shaderProgram, "u_alphaColor");

		GLES20.glEnableVertexAttribArray(a_position);
		GLES20.glEnableVertexAttribArray(a_texturePosition);

		GLES20.glVertexAttribPointer(a_position, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);
		GLES20.glVertexAttribPointer(a_texturePosition, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);
	}

	public void initDraw() {
		GLES20.glUseProgram(shaderProgram);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObject);
		bindAttributes();
	}

	public void setColor(Color c) { spriteColor.copy(c); }
	public void setColor(double r, double g, double b, double a) {
		spriteColor.copy(r, g, b, a);
	}

	public void setAlphaColor(Color c) { alphaColor.copy(c); }
	public void setAlphaColor(double r, double g, double b) {
		alphaColor.copy(r, g, b, 1.0);
	}

	public void reset() {
		Matrix.setIdentityM(modelMatrix, 0);
	}

	public void translate(double x, double y, double z) {
		Matrix.translateM(modelMatrix, 0, (float)x, (float)y, (float)z);
	}

	public void translate(Vertex v) {
		Matrix.translateM(modelMatrix, 0, (float)v.x, (float)v.y, 0f);
	}

	public void rotate(double a) {
		Matrix.rotateM(modelMatrix, 0, (float)a, 0f, 0f, 1f);
	}

	public void scale(double sx, double sy, double sz) {
		Matrix.scaleM(modelMatrix, 0, (float)sx, (float)sy, (float)sz);
	}

	public void scale(Vertex v) {
		Matrix.scaleM(modelMatrix, 0, (float)v.x, (float)v.y, 0f);
	}

	public void setPosition(float x, float y, float z) {
		reset();
		translate(x, y, z);
	}

	public void setPosition(Vertex v) {
		reset();
		translate(v);
	}

	public float[] getMVPMatrix() {
		float ret[] = new float[16];
		Matrix.multiplyMM(ret, 0, Game.viewProjection, 0, modelMatrix, 0);

		return ret;
}
	
	public void uploadData() {
		//spriteColor.confine();
		alphaColor.confine();

		GLES20.glUniformMatrix4fv(u_mvp, 1, false, getMVPMatrix(), 0);

		//Load texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandler);
		GLES20.glUniform1i(u_texture, 0);

		GLES20.glUniform4f(u_color, (float)spriteColor.r, (float)spriteColor.g, (float)spriteColor.b, (float)spriteColor.a);
		GLES20.glUniform3f(u_alphaColor, (float)alphaColor.r, (float)alphaColor.g, (float)alphaColor.b);
	}

	public void draw() {
		uploadData();
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	}
}