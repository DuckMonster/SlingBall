package com.emilstrom.slingball.game;

import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-25.
 */
public class RingEffect {
	Sprite ringSmall, ringBig;
	Sprite ring;
	double timer, timerMax, size;

	Vertex position;

	public RingEffect() {
		timer = 0;

		ringSmall = new Sprite(R.drawable.ring);
		ringBig = new Sprite(R.drawable.ringbig);
	}

	public void trigger(Vertex v, double t, double s) {
		size = s;
		position = new Vertex(v);
		timerMax = t;
		timer = timerMax;

		if (size > 2.0) ring = ringBig;
		else ring = ringSmall;
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

		ring.initDraw();

		ring.setPosition(position);
		ring.scale(1.0 + 3.0 * pe, 1.0 + 3.0 * pe, 0.0);
		ring.scale(size, size, 0.0);

		Color c = Game.getLightBackgroundColor(0.3);
		c.a -= p;

		ring.setColor(c);

		ring.draw();
	}
}
