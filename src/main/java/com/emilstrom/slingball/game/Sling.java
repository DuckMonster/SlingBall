package com.emilstrom.slingball.game;

import com.emilstrom.slingball.GameMath;
import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.Input;
import com.emilstrom.slingball.helper.InputHelper;
import com.emilstrom.slingball.helper.Sound;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-26.
 */
public class Sling extends GameObject {
	class Bullet {
		boolean launched = false;
		Vertex ballPosition, dragPosition;

		Vertex launchDir;
		double launchSpeed, launchPower, currentSpeed, launchDelay, launchDelayMax = 0.1;

		RingEffect ringEffect, bounceEffect[];
		HitEffect hitEffect;

		int bounceEffectn = 0;
		int bulletHealth = 0;

		boolean bounceProtection = false;

		public Bullet() {
			ballPosition = new Vertex();
			dragPosition = new Vertex();

			ringEffect = new RingEffect();
			hitEffect = new HitEffect();
			bounceEffect = new RingEffect[10];
			for(int i=0; i<bounceEffect.length; i++)
				bounceEffect[i] = new RingEffect();
		}

		public boolean isOnScreen() { return ballPosition.y <= 20 && ballPosition.y >= 0; }
		public boolean isAlive() { return bulletHealth > 0; }

		public void beginDrag(Vertex pos) {
			if (game.showMenus) game.startGame();

			launched = false;
			bulletHealth = 2;
			bounceProtection = false;

			ballPosition.copy(pos);
			dragPosition.copy(pos);
		}

		public void dragTo(Vertex pos) {
			dragPosition.copy(pos);
		}

		public void release() {
			launchDir = Vertex.getDirectionVertex(ballPosition, position);
			launchPower = Vertex.getLength(ballPosition, position) / maxSling;
			launchSpeed = 40.0 + 90.0 * launchPower;

			launched = true;
			launchDelay = launchDelayMax;
		}

		public void logic() {
			hitEffect.logic();
			ringEffect.logic();
			for(RingEffect r : bounceEffect)
				r.logic();

			if (!isAlive()) return;

			if (!launched) {
				currentSpeed = 0;

				Vertex speed = dragPosition.minus(ballPosition).times(50.0 * Game.updateTime);
				ballPosition.add(speed);
			} else {
				currentSpeed = launchSpeed;
				Vertex speed = launchDir.times(currentSpeed);

				int accuracy = 80;
				Vertex checkPosition = new Vertex(ballPosition);

				for(int i=0; i<accuracy; i++) {
					Vertex nextCheckPosition = Vertex.add(
							checkPosition,
							speed.times((1.0 / (double)accuracy) * Game.updateTime));

					//Hitting the ball
					Ball b = game.getBallCollision(nextCheckPosition);

					if (b != null && !bounceProtection) {
						b.hit(nextCheckPosition, launchPower, launchDir);

						ringEffect.trigger(nextCheckPosition, 0.6f, 1.1);
						hitEffect.trigger(nextCheckPosition, 0.6f, 1.1);

						launchDir = Vertex.getDirectionVertex(b.position, nextCheckPosition);
						bulletHealth--;
						bounceProtection = true;

						break;
					}

					if (nextCheckPosition.x < -Game.gameWidth/2 || nextCheckPosition.x > Game.gameWidth/2) {
						speed.x *= -1;
						launchDir.x *= -1;

						bounceProtection = false;

						if (isOnScreen()) {
							int bounceSound = GameMath.getRndInt(0, Sound.bounce.length-1);
							Sound.playSound(Sound.bounce[0], 0.5f);
							bounceEffect[bounceEffectn].trigger(checkPosition, 0.4, 0.4);
							bounceEffectn = (bounceEffectn+1)%bounceEffect.length;
						} else {
							float volume = 0f;
							if (ballPosition.y > 20) volume = (float)(1f - (ballPosition.y-20) * 0.05f);
							if (ballPosition.y < 0) volume = (float)(1f - (ballPosition.y * -1) * 0.05f);

							if (volume > 0f) {
								int bounceSound = GameMath.getRndInt(0, Sound.bounce.length-1);
								Sound.playSound(Sound.bounce[0], 0.5f * volume);
							}
						}

						break;
					}

					checkPosition = nextCheckPosition;
				}

				ballPosition = checkPosition;
			}
		}

		public void draw() {
			ringEffect.draw();
			hitEffect.draw();
			for(RingEffect r : bounceEffect)
				r.draw();

			if (!isAlive()) return;

			double dir, len;

			if (!launched) {
				dir = Vertex.getDirection(ballPosition, position);
				len = 0;
			} else {
				dir = launchDir.getDirection();
				len = launchDir.times(Math.min(currentSpeed, launchSpeed)).getLength();
			}

			sprite.setPosition(ballPosition);
			sprite.scale(0.8, 0.8, 0.0);

			sprite.rotate(dir);
			sprite.scale(1.0 + len / 20.0, 1.0, 0.0);
			sprite.setColor(Game.getForegroundColor());

			sprite.draw();
		}
	}



	///////////////

	public static final double slingAreaSize = 5.5, slingAreaMargin = 1.5, maxSling = 4.0, minSling = 1.2;

	Sprite slingArea, slingAreaFill,
		slingTri, bulletOrigin;

	Bullet bulletList[] = new Bullet[3];
	int bulletn = 0, ammo, ammoMax = 3;
	Input oldInput;
	boolean slinging = false;

	double slingAlpha = 0.0, ammoTimer, ammoInterval = 0.7, ammoReplenishTimer = 0.0, ammoReplenishTimerMax = 0.3, areaHint = 0.0, areaHintMax = 0.5;
	double originRotation = 0.0;

	RingEffect ammoReplenishEffect;

	public Sling(Game g) {
		super(0f, 0f, g);
		sprite.setTexture(R.drawable.ball);

		for(int i=0; i<bulletList.length; i++)
			bulletList[i] = new Bullet();

		ammo = ammoMax;
		ammoTimer = ammoInterval;

		ammoReplenishEffect = new RingEffect();

		slingArea = new Sprite(R.drawable.slingarea2);
		slingAreaFill = new Sprite(R.drawable.slingarea);

		slingTri = new Sprite(R.drawable.sling);
		bulletOrigin = new Sprite(R.drawable.ballorigin2);
	}

	public void logic() {
		Input in = InputHelper.getInput();
		if (oldInput == null) oldInput = in;

		if (in.pressed) {
			if (!oldInput.pressed && in.position.y > slingAreaSize+slingAreaMargin) {
				if (!game.showMenus || !game.soundButton.collidesWith(in.position))
					areaHint = areaHintMax;
			}
			if (!oldInput.pressed && in.position.y <= slingAreaSize+slingAreaMargin && ammo > 0) createBall(new Vertex(in.position));

			if (slinging) {
				Vertex pos = new Vertex(in.position);

				if (Vertex.getLength(pos, position) > maxSling) {
					Vertex dir = Vertex.getDirectionVertex(position, pos);
					pos = new Vertex(position.plus(dir.times(maxSling)));
				}

				bulletList[bulletn].dragTo(pos);

				originRotation += Game.updateTime * Vertex.subtract(pos, position).getLength();
			}
		}

		if (!in.pressed && oldInput.pressed && slinging) releaseBall();

		if (slinging) slingAlpha = 1.0;
		else slingAlpha -= Game.updateTime;

		if (ammo < ammoMax) {
			ammoTimer -= Game.updateTime;
			if (ammoTimer <= 0) {
				ammo++;
				ammoTimer = ammoInterval;
				ammoReplenishTimer = ammoReplenishTimerMax;
			}
		}

		ammoReplenishTimer -= Game.updateTime;
		areaHint -= Game.updateTime;

		for(Bullet b : bulletList) if (b!=null) b.logic();

		oldInput = in;
	}

	public void createBall(Vertex v) {
		if (v.y > slingAreaSize) v.y = slingAreaSize;

		slinging = true;
		bulletList[bulletn].beginDrag(v);
		position.copy(v);
	}

	public void releaseBall() {
		slinging = false;

		if (Vertex.getLength(position, bulletList[bulletn].ballPosition) < minSling) {
			bulletList[bulletn].bulletHealth = 0;
			return;
		}

		bulletList[bulletn].release();
		bulletn = (bulletn + 1) % bulletList.length;

		ammo--;
	}

	public void draw() {
		slingArea.initDraw();
		slingArea.reset();
		slingArea.scale(Game.gameWidth, slingAreaSize, 0.0);
		slingArea.translate(0.0, 0.5, 0.0);
		slingArea.setColor(Game.getBackgroundColor().times(new Color(1.0, 1.0, 1.0, 0.8)));

		slingArea.draw();

		slingAreaFill.initDraw();
		slingAreaFill.reset();
		slingAreaFill.scale(Game.gameWidth, slingAreaSize, 0.0);
		slingAreaFill.translate(0.0, 0.5, 0.0);

		Color areaColor = Game.getBackgroundColor().times(new Color(1.0, 1.0, 1.0, 0.3));

		if (areaHint > 0.0) areaColor = Color.blend(areaColor, new Color(1.0, 1.0, 1.0, 0.5), areaHint/areaHintMax);
		slingAreaFill.setColor(areaColor);

		slingAreaFill.draw();

		if (slinging) {
			Vertex v = Vertex.subtract(bulletList[bulletn].ballPosition, position);

			double dir = v.getDirection(),
					dis = v.getLength();

			Color c = Game.getForegroundColor();
			if (dis < minSling) c.a = 0.4;

			double origSize = (1 - (dis / maxSling)) * 1.5;
			dis -= origSize/2;

			double rot = originRotation * 100;

			bulletOrigin.initDraw();
			bulletOrigin.setPosition(position);

			bulletOrigin.setColor(c);
			bulletOrigin.rotate(rot);
			bulletOrigin.scale(1.5, 1.5, 1.0);

			bulletOrigin.draw();

			sprite.initDraw();
			sprite.setPosition(position);

			sprite.setColor(c);
			sprite.rotate(rot);
			sprite.scale(origSize * (1-dis/maxSling), origSize * (1-dis/maxSling), 1.0);

			sprite.draw();

			slingTri.initDraw();
			slingTri.setPosition(position);
			slingTri.rotate(dir+90);
			slingTri.translate(0.0, origSize/2, 0.0);
			slingTri.translate(0.0, dis/2, 0.0);
			slingTri.scale(0.8, dis, 0.8);

			c.a = dis / maxSling;
			slingTri.setColor(c);

			slingTri.draw();
		}

		Vertex ammoPos = new Vertex(-Game.gameWidth/2 + 0.6, slingAreaSize + 0.6);

		for(int i=0; i<ammo; i++) {
			sprite.initDraw();

			sprite.setPosition(ammoPos);
			sprite.translate(1.0 * i, 0.0, 0.0);
			sprite.scale(0.5, 0.5, 0.0);

			if (i == ammo-1 && ammoReplenishTimer > 0.0) {
				double sc = 1-(ammoReplenishTimer / ammoReplenishTimerMax);
				sc = Math.pow(Math.E, -sc*5.0);
				sprite.scale(1.0 + sc * 3.2, 1.0 + sc * 3.2, 0.0);
			}

			sprite.translate(0.2, -0.2, 0.0);
			sprite.setColor(new Color(0.0, 0.0, 0.0, 0.2));
			sprite.draw();

			sprite.translate(-0.2, 0.2, 0.0);
			sprite.setColor(Game.getForegroundColor());
			sprite.draw();
		}

		for(Bullet b : bulletList) if (b!=null) b.draw();
	}
}
