package com.emilstrom.slingball.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.emilstrom.slingball.SlingBall;

/**
 * Created by Emil on 2014-02-19.
 */
public class TextureHelper {
	public static int loadTexture(int resourceID) { return loadTexture(resourceID, null); }
	public static int loadTexture(int resourceID, int size[]) {
		int textureHandle[] = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;

			Bitmap b = BitmapFactory.decodeResource(SlingBall.context.getResources(), resourceID, options);

			if (size != null) {
				size[0] = b.getWidth();
				size[1] = b.getHeight();
			}

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, b, 0);

			b.recycle();
		}

		return textureHandle[0];
	}
}