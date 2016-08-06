package com.emilstrom.slingball.helper;

import android.media.AudioManager;
import android.media.SoundPool;

import com.emilstrom.slingball.R;
import com.emilstrom.slingball.SlingBall;

/**
 * Created by Emil on 2014-03-10.
 */
public class Sound {
	public static boolean soundIsOn = true;

	static SoundPool soundPool;
	public static int bounce[] = new int[1], hit[] = new int[5], lost, ballKill;

	public static void loadSounds() {
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		bounce[0] = soundPool.load(SlingBall.context, R.raw.bounce1, 1);

		hit[0] = soundPool.load(SlingBall.context, R.raw.hit1, 1);
		hit[1] = soundPool.load(SlingBall.context, R.raw.hit2, 1);
		hit[2] = soundPool.load(SlingBall.context, R.raw.hit3, 1);
		hit[3] = soundPool.load(SlingBall.context, R.raw.hit4, 1);
		hit[4] = soundPool.load(SlingBall.context, R.raw.hit5, 1);

		lost = soundPool.load(SlingBall.context, R.raw.lost, 1);
		ballKill = soundPool.load(SlingBall.context, R.raw.balldie4, 1);
	}

	public static void playSound(int n, float volume) {
		if (!soundIsOn || !SlingBall.isFocused) return;
		soundPool.play(n, volume, volume, 1, 0, 1.0f);
	}
}
