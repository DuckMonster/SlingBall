package com.emilstrom.slingball.helper;

import android.opengl.Matrix;

import com.emilstrom.slingball.game.Game;

/**
 * Created by Emil on 2014-02-19.
 */
public class InputHelper {
	static boolean pressed;
	static float x, y;

	public static void setPressed(boolean p) { pressed = p; }
	public static void setPosition(float xx, float yy) { x = xx; y = yy; }

	public static Input getInput() {
		float gameCoords[] = getGameCoords(x, y);
		return new Input(gameCoords[0], gameCoords[1], pressed);
	}

	public static float[] getGameCoords(float x, float y) {
		float coords[] = {x, y, 0f, 1f},
				invVP[] = new float[16];

		Matrix.invertM(invVP, 0, Game.viewProjection, 0);

		Matrix.multiplyMV(coords, 0, invVP, 0, coords, 0);
		return new float[]{coords[0], coords[1]};
	}
}
