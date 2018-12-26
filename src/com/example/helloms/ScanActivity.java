package com.example.helloms;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.moodstocks.android.ManualScannerSession;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;

public class ScanActivity extends Activity implements
		ManualScannerSession.Listener {

	private static final int TYPES = Result.Type.IMAGE | Result.Type.QRCODE
			| Result.Type.EAN13;
	String res = null;
	private ManualScannerSession session = null;
	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		SurfaceView preview = (SurfaceView) findViewById(R.id.preview);

		try {
			session = new ManualScannerSession(this, Scanner.get(), this,
					preview);
			session.setResultTypes(TYPES);
		} catch (MoodstocksError e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		session.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		session.stop();
	}

	public void onPreviewTapped(View v) {
		if (v.getId() == R.id.preview) {
			session.snap();
		}
	}

	@Override
	public void onCameraOpenFailed(Exception e) {
		// You should inform the user if this occurs!
	}

	@Override
	public void onWarning(String debugMessage) {
		// Useful for debugging!
	}

	@Override
	public void onError(MoodstocksError error) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT)
				.show();
		session.resume();
	}

	@Override
	public void onResult(final Result result, Bitmap queryFrame) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setNeutralButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				byte[] data = Base64.decode(result.getValue(), Base64.DEFAULT);
				try {
					String text = new String(data, "UTF-8");
					Intent videoIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(text));
					
					startActivity(videoIntent);

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// session.resume();
			}
		});
		if (result != null) {
			builder.setTitle(result.getType() == Result.Type.IMAGE ? "Image:"
					: "Barcode:");
			builder.setMessage(result.getValue());
			res = result.getValue();

			Log.e("KEYYYY", "" + res);

		} else {
			builder.setMessage("No result found");
		}
		builder.show();
	}

	@Override
	public void onServerSearchStart() {
		progressDialog = ProgressDialog.show(this, null, "Searching...", true,
				true, new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						session.cancel();
					}
				});
	}
}