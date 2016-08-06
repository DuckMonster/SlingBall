package com.emilstrom.slingball.game;

import android.opengl.Matrix;

/**
 * Created by Emil on 2014-02-18.
 */
public class Camera {
	float x, y, z;

	public Camera(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float[] getViewMatrix() {
		float view[] = new float[16];
		Matrix.setLookAtM(view, 0, x, y, z, x, y, 0f, 0f, 1f, 0f);
		return view;
	}
}
