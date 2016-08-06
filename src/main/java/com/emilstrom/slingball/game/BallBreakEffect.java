package com.emilstrom.slingball.game;

import com.emilstrom.slingball.GameMath;
import com.emilstrom.slingball.R;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-02-25.
 */
public class BallBreakEffect {
	Sprite ballPieceSprite[] = {
			new Sprite(R.drawable.ballpiece1),
			new Sprite(R.drawable.ballpiece2),
			new Sprite(R.drawable.ballpiece3),
			new Sprite(R.drawable.ballpiece4),
			new Sprite(R.drawable.ballpiece5)
	};

	class BallBreakEffect_Piece {
		static final double ground = -2.0;
		static final double gravity = 16.0;

		Sprite s;
		Vertex position, speed;
		double rotation, rotationSpeed, fragmentSize, colorVari,
			alpha, alphaMax, zposition;

		public BallBreakEffect_Piece(Vertex pos, Vertex vel, int sprite) {
			s = ballPieceSprite[sprite];
			position = new Vertex(pos);
			rotation = GameMath.getRndDouble(0.0, 360.0);
			rotationSpeed = GameMath.getRndDouble(-360.0, 360.0);

			speed = new Vertex(vel);
			speed.add(new Vertex(GameMath.getRndDouble(-25.0, 25.0), GameMath.getRndDouble(-25.0, 25.0)));

			fragmentSize = GameMath.getRndDouble(0.2, 0.9);
			colorVari = 1.0;

			alphaMax = GameMath.getRndDouble(0.4, 1.2);
			alpha = alphaMax;

			s.setColor(Game.getForegroundColor());

			zposition = GameMath.getRndDouble(0.01, 0.2);
		}

		public void logic() {
			if (position.y < ground || alpha <= 0) return;

			if (position.x + speed.x*Game.updateTime > Game.gameWidth/2 || position.x + speed.x*Game.updateTime < -Game.gameWidth/2) {
				speed.x *= -0.2;
				rotationSpeed *= -1;
			}

			speed.y -= gravity * Game.updateTime;

			position.add(speed.times(Game.updateTime));
			rotation += rotationSpeed * Game.updateTime;
			rotationSpeed -= rotationSpeed * 0.1 * Game.updateTime;

			//speed.subtract(new Vertex(speed.times(0.8)).times(Game.updateTime));

			alpha -= Game.updateTime * 0.4;
		}

		public void draw() {
			if (position.y < ground || alpha <= 0) return;

			s.initDraw();
			s.setPosition(position);

			s.scale(size, size, 0.0);
			s.scale(fragmentSize, fragmentSize, 0.0);

			//SHADOW
			s.translate(zposition, -zposition, 0.0);
			s.setColor(new Color(0.0, 0.0, 0.0, 0.2 * alpha / alphaMax));
			s.rotate(rotation);
			s.draw();
			s.rotate(-rotation);
			s.translate(-zposition, zposition, 0.0);

			s.rotate(rotation);

			s.setColor(Game.getForegroundColor().times(new Color(colorVari, colorVari, colorVari, alpha / alphaMax)));

			s.draw();
		}
	}

	BallBreakEffect_Piece ballPieceList[];
	double size;

	public BallBreakEffect() {
	}

	public void trigger(Vertex p, Vertex v, double s) {
		size = s;

		ballPieceList = new BallBreakEffect_Piece[GameMath.getRndInt(13, 20)];
		for(int i=0; i<ballPieceList.length; i++)
			ballPieceList[i] = new BallBreakEffect_Piece(p, v, GameMath.getRndInt(0, ballPieceSprite.length-1));
	}

	public void logic() {
		if (ballPieceList != null)
			for(BallBreakEffect_Piece p : ballPieceList)
				if (p != null) p.logic();
	}

	public void draw() {
		if (ballPieceList != null)
			for(BallBreakEffect_Piece p : ballPieceList)
				if (p != null) p.draw();
	}
}