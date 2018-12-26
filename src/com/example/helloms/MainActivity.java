package com.example.helloms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Scanner;

public class MainActivity extends Activity implements Scanner.SyncListener {

	// Moodstocks API key/secret pair
	private static final String API_KEY = "sepw4hpqykekzoozudli";
	private static final String API_SECRET = "r6kcXsS1LbnvaQj5";
	
	private boolean compatible = false;
	private Scanner scanner;

	@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    TextView tvTop = (TextView) findViewById(R.id.textView2);
    TextView tvBottam = (TextView) findViewById(R.id.textView3);
    Typeface typeFace= Typeface.createFromAsset(getAssets(),  "DroidNaskh-Regular.ttf");
    Typeface typeProxim= Typeface.createFromAsset(getAssets(),  "Roboto-Light.ttf");
    tvTop.setTypeface(typeFace);
    tvBottam.setTypeface(typeProxim);
    
    
    compatible = Scanner.isCompatible();
    if (compatible) {
      try {
        scanner = Scanner.get();
        String path = Scanner.pathFromFilesDir(this, "scanner.db");
        scanner.open(path, API_KEY, API_SECRET);
        scanner.setSyncListener(this);
        scanner.sync();
      } catch (MoodstocksError e) {
        e.printStackTrace();
      }
    }
  }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (compatible) {
			try {
				scanner.close();
				scanner.destroy();
				scanner = null;
			} catch (MoodstocksError e) {
				e.printStackTrace();
			}
		}
	}

	public void onScanButtonClicked(View view) {
		// if (compatible) {
		// startActivity(new Intent(this, ScanActivity.class));
		// }

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri = Uri.parse("file:///sdcard/Saurabh/abc.jpg");
		// sharingIntent.setType("image/*");
		sharingIntent.setType("image");
		// sharingIntent.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		startActivity(Intent.createChooser(sharingIntent, "Share imageusing"));
	}

	@Override
	public void onSyncStart() {
		Log.d("Moodstocks SDK", "Sync will start.");
	}

	@Override
	public void onSyncComplete() {
		try {
			Log.d("Moodstocks SDK", "Sync succeeded (" + scanner.count() + " images)");
		} catch (MoodstocksError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSyncFailed(MoodstocksError e) {
		Log.d("Moodstocks SDK", "Sync error #" + e.getErrorCode() + ": " + e.getMessage());
	}

	@Override
	public void onSyncProgress(int total, int current) {
		int percent = (int) ((float) current / (float) total * 100);
		Log.d("Moodstocks SDK", "Sync progressing: " + percent + "%");
	}

}