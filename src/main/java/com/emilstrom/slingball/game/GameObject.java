package com.emilstrom.slingball.game;

import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-19.
 */
public class GameObject {
	Game game;
	Sprite sprite;
	Vertex position;

	public GameObject(float x, float y, Game g) {
		position = new Vertex(x, y);
		game = g;

		sprite = new Sprite(R.drawable.ball);
	}

	public void logic() {
	}

	public void draw() {
		sprite.setPosition(position);
		sprite.draw();
	}
}
