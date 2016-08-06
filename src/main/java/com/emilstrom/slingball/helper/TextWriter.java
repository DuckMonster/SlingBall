package com.emilstrom.slingball.helper;

import com.emilstrom.slingball.game.TiledSprite;

/**
 * Created by Emil on 2014-02-25.
 */
public class TextWriter {
	static Vertex textScale = new Vertex(1.0, 1.0);
	static TiledSprite tileSet = new TiledSprite();

	public static void loadText() { tileSet = new TiledSprite(); }

	public static void setColor(Color c) { tileSet.setColor(c); }
	public static void setScale(Vertex v) { textScale.copy(v); }

	public static void drawText(float x, float y, String s) { drawText(new Vertex(x, y), s); }
	public static void drawText(Vertex pos, String s) {
		char text[] = s.toCharArray();

		tileSet.initDraw();
		tileSet.setPosition(pos);
		tileSet.scale(textScale);
		tileSet.scale(2f, 2f, 0f);
		tileSet.translate(-0.5f * (text.length - 1), 0.0, 0.0);

		int charsDrawn = 0;
		for(char c : text) {
			if (c >= '0' && c <= '9') {
				tileSet.draw(c - '0', 0);
				tileSet.translate(1f, 0f, 0f);

				charsDrawn++;
			}
		}
	}
}
