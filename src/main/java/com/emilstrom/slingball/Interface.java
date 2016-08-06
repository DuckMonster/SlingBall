package com.emilstrom.slingball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Emil on 2014-02-21.
 */
public class Interface extends ViewGroup {
	TextView currentScore;

	class Score extends View {
		Paint paint;

		public Score() {
			super(SlingBall.context);

			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(0xFFFFFFFF);
		}

		@Override
		protected void onDraw(Canvas c) {
			Log.v(SlingBall.TAG, "Drawin");

			float s = (float)(Math.sin(SystemClock.uptimeMillis() * 0.001) * 60.0);
			paint.setTextSize(s);

			c.drawText(Float.toString(s), 2, 20, paint);
		}
	}

	Score score;

	public Interface() {
		super(SlingBall.context);
		score = new Score();

		this.addView(score);
		this.setBackgroundColor(0x000000FF);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		setPadding(l, t, r, b);
	}
}
