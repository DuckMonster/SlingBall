package com.emilstrom.slingball;

import com.emilstrom.slingball.game.Game;
import com.emilstrom.slingball.game.Sling;
import com.emilstrom.slingball.game.Sprite;
import com.emilstrom.slingball.helper.Color;
import com.emilstrom.slingball.helper.Input;
import com.emilstrom.slingball.helper.InputHelper;
import com.emilstrom.slingball.helper.Sound;
import com.emilstrom.slingball.helper.Vertex;

/**
 * Created by Emil on 2014-03-13.
 */
public class SoundButton {
	Game game;

	Vertex position, size;
	Sprite soundOn, soundOff;

	Input oldInput;

	double alpha = 0.3, alphaMin = 0.3;

	public SoundButton(Game g) {
		game = g;

		size = new Vertex(2.0, 2.0);
		position = new Vertex(-Game.gameWidth + 1.0 + size.x/2, 20 - 1.0 - size.y/2);
		soundOn = new Sprite(R.drawable.soundon);
		soundOff = new Sprite(R.drawable.soundoff);

		Sound.soundIsOn = game.pref.getBoolean("sound", true);
	}

	public boolean collidesWith(Vertex v) {
		return (v.x >= position.x - size.x/2 &&
				v.x < position.x + size.x/2 &&
				v.y >= position.y - size.y/2 &&
				v.y < position.y + size.y/2);
	}

	public void logic() {
		position = new Vertex(Game.gameWidth/2 - 0.4 - size.x/2, Sling.slingAreaSize + 0.4 + size.y/2);

		Input in = InputHelper.getInput();
		if (oldInput == null) oldInput = in;

		if (in.pressed && !oldInput.pressed) {
			if (collidesWith(in.position)) {
				Sound.soundIsOn = !Sound.soundIsOn;
				alpha = 1.0;
				Sound.playSound(Sound.hit[1], 0.9f);

				game.prefEditor.putBoolean("sound", Sound.soundIsOn);
				game.prefEditor.commit();
			}
		}

		alpha -= Game.updateTime * 1.4;
		if (alpha < alphaMin) alpha = alphaMin;

		oldInput = in;
	}

	public void draw() {
		Sprite s = Sound.soundIsOn ? soundOn : soundOff;

		s.initDraw();
		s.setPosition(position);

		double a = alpha * game.startGameAlpha;
		double alphaPerc = 1 - (alpha-alphaMin) / (1.0 - alphaMin);
		double scale = Math.pow(Math.E, -alphaPerc * 20.0) * 0.4;

		s.scale(1.0 + scale, 1.0 + scale, 1.0);
		s.scale(size);

		s.translate(0.05, -0.05, 0.0);
		s.setColor(new Color(0.0, 0.0, 0.0, 0.4 * a));
		s.draw();
		s.translate(-0.05, 0.05, 0.0);

		Color c = Color.blend(new Color(1.0, 1.0, 1.0, 1.0), game.getBackgroundLevel(), alphaPerc);
		c.a = a;

		s.setColor(c);

		s.draw();
	}
}
