package com.emilstrom.slingball.game;

import com.emilstrom.slingball.GameMath;
import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-25.
 */
public class HitEffect {
	Sprite spr;
	double timer, timerMax, randomRot, size;

	Vertex position;

	public HitEffect() {
		spr = new Sprite(R.drawable.hit);
		timer = 0;
	}

	public void trigger(Vertex v, double t, double s) {
		size = s;
		position = new Vertex(v);
		timerMax = t;
		timer = timerMax;

		randomRot = GameMath.getRndDouble(0.0, 90.0);
	}

	public double getTimerPerc() { return (timerMax - timer) / timerMax; }

	public void logic() {
		if (timer <= 0) return;
		timer -= Game.updateTime;
	}

	public void draw() {
		if (timer <= 0) return;
		double p = getTimerPerc(),
				pe = 1-Math.pow(Math.E, -p * 5.0);

		spr.initDraw();

		for(int i=0; i<4; i++) {
			spr.setPosition(position);
			spr.rotate(randomRot + 90.0 * i);
			spr.scale(size, size, 0.0);
			spr.translate(0.0, 3.5 * pe, 0.0);
			spr.scale(1.0 - 1.0*p, 1.0 - 1.0*p, 0.0);

			Color c = Game.getLightBackgroundColor(0.3);
			c.a -= p;

			spr.setColor(c);

			spr.draw();
		}
	}
}
