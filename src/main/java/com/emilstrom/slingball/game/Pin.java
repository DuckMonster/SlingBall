package com.emilstrom.slingball.game;

import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Input;
import com.emilstrom.slingball.helper.InputHelper;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-19.
 */
public class Pin extends GameObject {
	Vertex pinPosition = new Vertex(),
		pinSpeed = new Vertex(),
		releaseDir = new Vertex();

	double releasePower, pinFriction = 40f, maxPull = 3f;
	boolean held = false, released = false;

	Input oldIn;

	RingEffect ringEffect;
	HitEffect hitEffect;

	public Pin(float x, float y, Game g) {
		super(x, y, g);

		sprite.setTexture(R.drawable.ball);

		ringEffect = new RingEffect();
		hitEffect = new HitEffect();
	}

	public void logic() {
		Input in = InputHelper.getInput();
		if (oldIn == null) oldIn = in;

		if (in.pressed) {
			if (Vertex.getLength(position, in.position) <= 1.6f) held = true;
		}

		if (held && in.pressed) {
			pinPosition = Vertex.subtract(in.position, position);
			pinSpeed = new Vertex();

			if (pinPosition.getLength() > maxPull) {
				Vertex dir = Vertex.normalize(pinPosition);
				pinPosition = Vertex.multiply(dir, maxPull);
			}
		}

		if (held && !in.pressed && oldIn.pressed) {
			held = false;

			Vertex dir = Vertex.getDirectionVertex(pinPosition, new Vertex(0, 0));
			double dis = pinPosition.getLength();

			releasePower = dis * 1.7f;
			releaseDir = dir;

			pinSpeed = new Vertex(Vertex.multiply(dir, dis * 40f));

			released = true;
		}

		if (!held) {
			pinPosition.add(Vertex.multiply(pinSpeed, Game.updateTime));

			if (released) {
				//Check for ball collision!
				int accuracy = 80;
				Vertex checkPosition = new Vertex(pinPosition);

				for(int i=0; i<accuracy; i++) {
					Vertex nextCheckPosition = Vertex.add(
							checkPosition,
							pinSpeed.times((1 / accuracy) * Game.updateTime));

					//Hitting the ball
					Ball b = game.getBallCollision(position.plus(nextCheckPosition));

					if (b != null) {
						b.hit(position.plus(nextCheckPosition), releasePower, releaseDir);
						releaseHit(nextCheckPosition);

						ringEffect.trigger(position.plus(nextCheckPosition), 0.6f, 1.0);
						hitEffect.trigger(position.plus(nextCheckPosition), 0.6f, 1.0);

						break;
					}

					checkPosition = nextCheckPosition;
				}
			} else {
				Vertex dir = Vertex.getDirectionVertex(pinPosition, new Vertex());
				double len = pinPosition.getLength();

				pinSpeed.add(dir.times(len * 20 * Game.updateTime));
				pinSpeed.subtract(pinSpeed.times(2.5 * Game.updateTime));
			}
		}

		oldIn = in;

		ringEffect.logic();
		hitEffect.logic();
	}

	public void releaseHit(Vertex pos) {
		released = false;

		pinPosition = pos;

		pinSpeed = Vertex.multiply(Vertex.multiply(releaseDir, -1f), 5f * releasePower);
	}

	public void draw() {
		sprite.initDraw();

		sprite.reset();
		sprite.translate(position.x, position.y, -1.f);
		sprite.scale(1.2, 1.2, 0.0);

		sprite.setColor(0f, 0f, 0f, 1f);

		sprite.draw();

		sprite.setPosition(position.plus(pinPosition));
		sprite.scale(0.8f, 0.8f, 0.98);

		sprite.setColor(Game.getForegroundColor());
		sprite.draw();

		ringEffect.draw();
		hitEffect.draw();
	}
}
