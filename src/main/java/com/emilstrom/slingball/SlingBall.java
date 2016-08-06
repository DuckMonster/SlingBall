package com.emilstrom.slingball;

import android.app.Activity;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class SlingBall extends Activity {
	public static final String TAG = "SlingBallLog";
	private static final String AD_ID = "ca-app-pub-9176069288033174/7422435241";

	public static boolean isFocused = false;

	private static AdView adView;
	private static AdRequest adRequest;

	public static SlingBall context;
	public static Interface interf;
	private GameSurface surface;

	private float adX, adY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sling_ball);

		context = this;
		surface = new GameSurface(this);

		interf = new Interface();

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		//Ads
		adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_ID);

		Display dis = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		dis.getSize(p);

		adX = 0;
		adY = -p.y/2 + 50;
		adView.setY(adY);

		adRequest = new AdRequest.Builder()
//				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//				.addTestDevice("943474A329612F841F7D60EAB67C63C6")
				.build();

		adView.loadAd(adRequest);

		FrameLayout frame = (FrameLayout)findViewById(R.id.mainFrame);

		//frame.setTop

		frame.addView(surface);
		frame.addView(new Interface());
		frame.addView(adView);
    }

	public void hideAd() {
		runOnUiThread(new Runnable() {
			public void run() {
				adView.setY(adY - 200f);
			}
		});
	}

	public void showAd() {
		runOnUiThread(new Runnable() {
			public void run() {
				adView.loadAd(adRequest);
				adView.setY(adY);
			}
		});
	}

	@Override
	protected void onResume() {
		isFocused = true;
		surface.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		isFocused = false;
		super.onPause();
	}
}