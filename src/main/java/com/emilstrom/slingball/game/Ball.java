package com.emilstrom.slingball.game;

import android.os.SystemClock;

import com.emilstrom.slingball.GameMath;
import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.Sound;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-19.
 */
public class Ball extends GameObject {
	Sprite ballArrow, smiley;
	Vertex speed;
	TiledSprite healthSprite;

	int ballHealth, spawnBallHealth;

	double size = 1.0;

	double rotation;
	double rotateSpeed;
	double rotateFriction = 45;
	double xFriction = 0.4;
	double gravity = 30.0;

	double hitTimer = 0f, hitTimerMax = 0.4f, killedTimer=0.0, killedTimerMax=0.5;

	RingEffect killEffect, spawnEffect;
	BallBreakEffect breakEffect;

	boolean isCustom = true;

	public Ball(Game g) {
		super(0f, 0f, g);
		healthSprite = new TiledSprite();

		sprite.setColor(Game.getForegroundColor());

		breakEffect = new BallBreakEffect();
		killEffect = new RingEffect();
		spawnEffect = new RingEffect();

		ballArrow = new Sprite(R.drawable.sling);

		smiley = new Sprite(R.drawable.smiley);
	}

	public boolean noHealth() { return ballHealth <= 0; }
	public boolean isDead() { return noHealth() && killedTimer <= 0; }

	public boolean collidesWith(float xx, float yy) {
		return collidesWith(new Vertex(xx, yy));
	}
	public boolean collidesWith(Vertex v) {
		if (isDead()) return false;
		return (Vertex.getLength(position, v) <= size/2);
	}

	public void hit(Vertex pos, double power, Vertex dir) {
		Vertex hitDir = Vertex.getDirectionVertex(pos, position);

		//Get rotation
		double dirDif = dir.getDirection() - hitDir.getDirection();
		rotateSpeed = -dirDif * 3.0;

		hitDir = Vertex.normalize(Vertex.add(hitDir.times(1.0), dir.times(1.1)));

		power += 1f;
		speed = Vertex.multiply(hitDir, power*10.0);

		//Health down
		if (ballHealth > 0)
			ballHealth--;

		hitTimer = hitTimerMax;

		int hitSound = GameMath.getRndInt(0, Sound.hit.length-1);
		Sound.playSound(Sound.hit[hitSound], 1f);

		if (noHealth()) {
			killedTimer = killedTimerMax;
		}
	}

	public void die() {
		Sound.playSound(Sound.ballKill, 0.4f);

		game.score();

		killEffect.trigger(position, 1.0, 4.0);
		breakEffect.trigger(position, speed, size);
	}

	public void lose() {
		killEffect.trigger(position, 2.0, 5.0);
		breakEffect.trigger(position, new Vertex(0.0, 10.0), size);

		ballHealth = 0;
	}

	public void respawn() {
		position = new Vertex(0, 16);
		speed = new Vertex(GameMath.getRndDouble(-15, 15), 14);
		rotateSpeed = GameMath.getRndDouble(-400, 400);

		double rndSize = GameMath.getRndDouble(0.0, 1.0),
				minSize = 3.2,
				maxSize = 4.4;

		int minHealth = 3,
				maxHealth = 5;

		size = rndSize * (maxSize - minSize) + minSize;

		spawnBallHealth = (int)Math.round(rndSize * (maxHealth - minHealth) + minHealth);
		ballHealth = spawnBallHealth;

		spawnEffect.trigger(position, 0.6, 2.0);
	}

	public void logic() {
		breakEffect.logic();
		killEffect.logic();
		spawnEffect.logic();

		if (killedTimer > 0.0) {
			killedTimer -= Game.updateTime;
			if (killedTimer <= 0) {
				die();
			}
		}

		if (isDead()) return;

		speed.y -= gravity * Game.updateTime;

		double wallPos = Game.gameWidth/2 - size/2 + 0.2;
		if (position.x + speed.x*Game.updateTime > wallPos || position.x + speed.x*Game.updateTime < -wallPos)
			speed.x *= -0.8;

		if (position.y < 0 && !noHealth()) {
			lose();
			game.lose();
		}

		position.add(Vertex.multiply(speed, Game.updateTime));

		//Spin around!
		rotation += rotateSpeed * Game.updateTime;

		if (Math.abs(speed.x) < xFriction*Game.updateTime) speed.x = 0;
		if (speed.x != 0) {
			if (speed.x > 0) speed.x -= xFriction*Game.updateTime;
			else speed.x += xFriction*Game.updateTime;
		}

		if (Math.abs(rotateSpeed) < rotateFriction*Game.updateTime) rotateSpeed = 0;
		if (rotateSpeed != 0) {
			if (rotateSpeed > 0) rotateSpeed -= rotateFriction*Game.updateTime;
			else rotateSpeed += rotateFriction*Game.updateTime;
		}

		//HitTimer
		if (hitTimer > 0) hitTimer -= Game.updateTime;
		if (hitTimer < 0) hitTimer = 0;
	}

	public void draw() {
		//Draw arrow
		if (position.y > 20.0 && !noHealth()) {
			double a = (10.0 - (position.y - 20.0)) / 10.0;

			if (a > 0.0) {
				ballArrow.initDraw();

				ballArrow.setPosition(new Vertex(position.x, 19.0));
				ballArrow.scale(0.7, -0.7, 1.0);

				ballArrow.setColor(Game.getForegroundColor().times(new Color(1.0, 1.0, 1.0, a)));
				ballArrow.draw();
			}
		}

		breakEffect.draw();
		killEffect.draw();
		spawnEffect.draw();

		if (isDead()) return;

		sprite.initDraw();

		sprite.reset();

		sprite.translate(position);
		smiley.setPosition(position);

		if (noHealth()) {
			double xx = Math.cos(SystemClock.uptimeMillis() * 4);
			xx *= 1.0 - (killedTimer/killedTimerMax);
			sprite.translate(xx * 1.2, 0.0, 0.0);
			smiley.translate(xx * 1.2, 0.0, 0.0);

			double scalex = GameMath.getRndDouble(-0.8, 0.8);
			scalex *= 1.0 - (killedTimer/killedTimerMax);
			sprite.scale(1.0 + Math.abs(scalex), 1.0 - Math.abs(scalex), 0.0);
			smiley.scale(1.0 + Math.abs(scalex), 1.0 - Math.abs(scalex), 0.0);
		}

		sprite.scale(size, size, 0.0);

		sprite.translate(0.2, -0.2, 0.0);
		sprite.rotate(rotation);

		sprite.setColor(new Color(0.0, 0.0, 0.0, 0.2));
		sprite.draw();

		sprite.rotate(-rotation);
		sprite.translate(-0.2, 0.2, 0.0);

		sprite.setColor(Game.getForegroundColor());
		sprite.rotate(rotation);
		sprite.draw();

		if (hitTimer/hitTimerMax > 0.4 && !noHealth()) {
			sprite.setColor(1.0, 1.0, 1.0, hitTimer / hitTimerMax);
			sprite.draw();
		}

		if (noHealth()) {
			sprite.setColor(1.0, 1.0, 1.0, 1.0 - (killedTimer/killedTimerMax));
			sprite.draw();
		}

		if (game.getColorLvl() >= 11) {
			smiley.initDraw();
			smiley.scale(size, size, 0);
			smiley.rotate(rotation);
			smiley.setColor(new Color(0.0, 0.0, 0.0, 0.4));
			smiley.draw();
		}

		float textSize = 0.4f + 0.8f * (float)(hitTimer / hitTimerMax) +
				0.5f * (1f - (float)ballHealth / (float)spawnBallHealth),

				textOsc = (float)Math.cos(SystemClock.uptimeMillis() * 6) * (float)(hitTimer / hitTimerMax) * 0.2f;

		textSize *= size;

		healthSprite.initDraw();

		healthSprite.reset();
		healthSprite.translate(position);
		healthSprite.translate(textOsc, 0f, 0f);
		healthSprite.rotate(rotation);

		healthSprite.translate(textOsc, 0f, 0f);
		healthSprite.scale(textSize, textSize, 0.0);
		healthSprite.setColor(1.0, 1.0, 1.0, 1.0);

		healthSprite.draw(ballHealth, 0);
	}
}