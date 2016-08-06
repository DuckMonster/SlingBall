package com.emilstrom.slingball.game;

import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.emilstrom.slingball.R;
import com.emilstrom.slingball.SlingBall;
import com.emilstrom.slingball.SoundButton;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.ShaderHelper;
import com.emilstrom.slingball.helper.Sound;
import com.emilstrom.slingball.helper.TextWriter;
import com.emilstrom.slingball.helper.Vertex;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Emil on 2014-02-18.
 */
public class Game implements GLSurfaceView.Renderer {
	public static double updateTime;
	public static double gameWidth;

	public static float viewProjection[] = new float[16];

	public static int currentScore = 0, record = 0;
	private static Color currentBackgroundColor, currentForegroundColor;

	public static Color colorList[] = {
			new Color(0.5, 0.5, 0.5, 1.0),		//0
			new Color(0.7, 0.7, 0.7, 1.0),

			new Color(0.3, 0.7, 1.0, 1.0),		//1
			new Color(1.0, 0.4, 0.05, 1.0),

			new Color(32, 201, 139, 255),		//2
			new Color(6, 87, 252, 255),

			new Color(96, 220, 35, 255),		//3
			new Color(239, 208, 0, 255),

			new Color(255, 154, 255, 255),		//4
			new Color(255, 0, 160, 255),

			new Color(0, 102, 153, 255),		//5
			new Color(102, 204, 51, 255),

			new Color(0.8, 0.0, 0.0, 1.0),		//6
			new Color(255, 163, 0, 255),

			new Color(149, 38, 185, 255),		//7
			new Color(0, 138, 255, 255),

			new Color(28, 141, 105, 255),		//8
			new Color(155, 200, 0, 255),

			new Color(185, 38, 104, 255),		//9
			new Color(254, 35, 159, 255),

			new Color(38, 121, 185, 255),		//10
			new Color(0, 246, 255, 255),

			new Color(245, 202, 0, 255),		//11
			new Color(223, 70, 35, 255)
	};

	public static Color getBackgroundColor() { return new Color(currentBackgroundColor); }
	public static Color getForegroundColor() { return new Color(currentForegroundColor); }
	public static Color getLightBackgroundColor(double f) {
		Color c = new Color(currentBackgroundColor);
		c.r += f * (1.0 - c.r);
		c.g += f * (1.0 - c.g);
		c.b += f * (1.0 - c.b);
		return c;
	}
	public static Color getLightForegroundColor(double f) {
		Color c = new Color(currentForegroundColor);
		c.r += f * (1.0 - c.r);
		c.g += f * (1.0 - c.g);
		c.b += f * (1.0 - c.b);
		return c;
	}

	private float projection[] = new float[16];
	Camera cam;

	public Ball ball;
	public Sling sling;

	public SharedPreferences pref;
	public SharedPreferences.Editor prefEditor;
	private Sprite background, startText, logo;
	public SoundButton soundButton;
	
	public double scoreTimer = 0.0, scoreTimerMax = 1.0,
			ballRespawnTimer = 0.0, ballRespawnTimerMax = 1.4,
			loseTimer = 0.0, loseTimerMax = 2.0,
			gameStartTimer = 0.0, gameStartTimerMax = 0.6,
			startGameAlpha = 1.0,
			colorLevel = 0.0;

	boolean showMenus = true;

	public Game() {
		cam = new Camera(0f, 0f, 3f);
	}

	public void loadHighscore() {
		pref = PreferenceManager.getDefaultSharedPreferences(SlingBall.context);
		prefEditor = pref.edit();

		record = pref.getInt("scoreRecord", 0);
	}

	public void saveHighscore(int score) {
		prefEditor.putInt("scoreRecord", score);
		prefEditor.commit();
	}

	public void score() {
		currentScore++;
		scoreTimer = scoreTimerMax;

		ballRespawnTimer = ballRespawnTimerMax;
	}

	public void lose() {
		Sound.playSound(Sound.lost, 0.4f);
		SlingBall.context.showAd();
		loseTimer = loseTimerMax;
		showMenus = true;

		if (currentScore > record) {
			saveHighscore(currentScore);
		}
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		loadHighscore();
		colorLevel = getColorLvl();

		currentBackgroundColor = colorList[0];
		currentForegroundColor = colorList[1];

		GLES20.glClearColor(0f, 0f, 0f, 1f);
		ShaderHelper.loadShader();

		sling = new Sling(this);
		ball = new Ball(this);

		background = new Sprite(R.drawable.background4);
		background.setColor(currentBackgroundColor);

		startText = new Sprite(R.drawable.starttext);
		logo = new Sprite(R.drawable.logo);

		TextWriter.loadText();

		Sound.loadSounds();
		soundButton = new SoundButton(this);
	}

	public void respawn() {
		if (currentScore > record) record = currentScore;
		currentScore = 0;

		ballRespawnTimer = ballRespawnTimerMax;
	}

	public void startGame() {
		showMenus = false;
		SlingBall.context.hideAd();

		gameStartTimer = gameStartTimerMax;
		respawn();
	}

	public int getColorLvl() { return Math.min(Math.max(currentScore, record)/10 + 1, colorList.length/2-1); }
	//public int getColorLvl() { return 11; }

	public Color getBackgroundLevel() { return colorList[getColorLvl()*2]; }
	public Color getForegroundLevel() { return colorList[getColorLvl()*2+1]; }

	public void logic() {
		if (loseTimer > 0.0) {
			cam.x = (float)(Math.cos(SystemClock.uptimeMillis()) * 0.4 * (loseTimer / loseTimerMax));

			loseTimer -= Game.updateTime;
			if (loseTimer <= 0.0) {
				cam.x = 0;
			}
		}

		if (gameStartTimer > 0.0) {
			gameStartTimer -= Game.updateTime;

			currentBackgroundColor = Color.blend(getBackgroundLevel(), colorList[0], gameStartTimer/gameStartTimerMax);
			currentForegroundColor = Color.blend(getForegroundLevel(), colorList[1], gameStartTimer/gameStartTimerMax);

			if (gameStartTimer <= 0.0) {
				currentBackgroundColor = getBackgroundLevel();
				currentForegroundColor = getForegroundLevel();
			}
		}

		//COLOR FADING
		if (!showMenus && loseTimer <= 0 && gameStartTimer <= 0 && getColorLvl() < colorList.length) {
			int clrLvl = getColorLvl();

			if (clrLvl < (int)colorLevel) colorLevel = clrLvl;
			if ((int)colorLevel == clrLvl) {
				currentBackgroundColor = getBackgroundLevel();
				currentForegroundColor = getForegroundLevel();
			} else {
				colorLevel += Game.updateTime * 0.5;

				currentBackgroundColor = Color.blend(new Color(1.0, 1.0, 1.0, 1.0), getBackgroundLevel(), colorLevel-(clrLvl-1));
				currentForegroundColor = Color.blend(new Color(1.0, 1.0, 1.0, 1.0), getForegroundLevel(), colorLevel-(clrLvl-1));
			}
		}

		if (showMenus) {
			if (soundButton != null)
				soundButton.logic();

			currentBackgroundColor = colorList[0];
			currentForegroundColor = colorList[1];

			startGameAlpha += Game.updateTime * 0.6;
			if (startGameAlpha > 1.0) startGameAlpha = 1.0;
		} else {
			startGameAlpha -= Game.updateTime * 1.2;
			if (startGameAlpha < 0) startGameAlpha = 0;
		}

		if (ball != null)
			ball.logic();
		if (sling != null)
			sling.logic();

		scoreTimer -= Game.updateTime;

		if (ballRespawnTimer > 0 && ball != null) {
			ballRespawnTimer -= Game.updateTime;
			if (ballRespawnTimer <= 0) {
				ball.respawn();
			}
		}
	}

	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		viewProjection = getViewProj();

		//Background
		background.initDraw();

		background.setPosition(0f, 10f, 0f);
		background.scale(20f, 20f, 0f);
		background.setColor(currentBackgroundColor);

		background.draw();

		Color c = getLightBackgroundColor(0.1);

		double textScale = 1.0;

		if (currentScore > record && record != 0) {
			double xx = currentScore - record,
					sca = 1.0 - Math.pow(Math.E, -(xx * 0.3));

			textScale = 1.0 + sca * 0.5;

			double a = (Math.cos(SystemClock.uptimeMillis() * 0.002) + 1.0) / 2.0;
			c = Color.blend(c, new Color(1.0, 1.0, 1.0, 1.0), a);
		}

		TextWriter.setScale(new Vertex(textScale, textScale));

		if (scoreTimer <= 0) {
			TextWriter.setColor(c);
			TextWriter.drawText(0f, 10f, Integer.toString(currentScore));
		} else {
			double movePerc = (1-(scoreTimer / scoreTimerMax)) / 0.07;
			if (movePerc <= 1.0) {
				TextWriter.setScale(new Vertex(textScale*0.6, textScale*1.4));

				TextWriter.setColor(c.times(new Color(1.0, 1.0, 1.0, 1.0 - movePerc)));
				TextWriter.drawText(0f, 10f + 2f * (float)movePerc, Integer.toString(currentScore-1));

				TextWriter.setColor(new Color(1.0, 1.0, 1.0, movePerc));
				TextWriter.drawText(0f, 10f - 2f + 2f * (float)movePerc, Integer.toString(currentScore));
			} else {
				TextWriter.setColor(c);
				TextWriter.drawText(0f, 10f, Integer.toString(currentScore));

				TextWriter.setColor(new Color(1.0, 1.0, 1.0, (scoreTimer/scoreTimerMax)));
				TextWriter.drawText(0f, 10f, Integer.toString(currentScore));
			}
		}

		if (record >= currentScore && record != 0) {
			c.a -= 0.5;
			TextWriter.setScale(new Vertex(textScale, textScale));
			TextWriter.setColor(c);
			TextWriter.drawText(0f, 7.5f, Integer.toString(record));
		}

		if (startGameAlpha > 0.0) {
			double a = (Math.cos(SystemClock.uptimeMillis() * 0.005) + 1) / 2.0;
			a *= startGameAlpha;

			startText.initDraw();
			startText.setColor(new Color(1.0, 1.0, 1.0, a));

			startText.setPosition(0f, (float)Sling.slingAreaSize/2, 0f);
			startText.scale(Sling.slingAreaSize*2, Sling.slingAreaSize * 2, 0.0);

			startText.draw();

			double logoRot = Math.cos(SystemClock.uptimeMillis() * 0.0007919) * 20.0,
					logoScale = 1.0 + 0.1 * Math.cos(SystemClock.uptimeMillis() * 0.00139);

			Color logoColor = new Color(getBackgroundLevel());
			logoColor.a = startGameAlpha;

			logo.initDraw();

			logo.setPosition(0f, 14f, 0f);
			logo.scale(12f * logoScale, 12f * logoScale, 1f);

			logo.translate(0.05 * logoScale*2, -0.05 * logoScale*2, 0.0);
			logo.rotate(logoRot);
			logo.setColor(new Color(0.0, 0.0, 0.0, startGameAlpha * 0.4));
			logo.draw();

			logo.setColor(logoColor);

			logo.setPosition(0f, 14f, 0f);
			logo.scale(12f * logoScale, 12f * logoScale, 1f);
			logo.rotate(logoRot);

			logo.draw();

			soundButton.draw();
		}

		sling.draw();
		ball.draw();
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float)width / height,
			screenScale = 10;

		gameWidth = ratio * screenScale * 2;

		Matrix.orthoM(projection, 0, -ratio*screenScale, ratio*screenScale, 0, screenScale*2, 3, 7);
	}

	public float[] getViewProj() {
		float viewProj[] = new float[16];
		Matrix.multiplyMM(viewProj, 0, projection, 0, cam.getViewMatrix(), 0);
		return viewProj;
	}

	public Ball getBallCollision(Vertex v) {
		if (ball.collidesWith(v)) return ball;
		return null;
	}
}
