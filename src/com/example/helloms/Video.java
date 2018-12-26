package com.example.helloms;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class Video extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		if (getIntent().getData() != null) {

			Uri uri = Uri.parse(getIntent().getExtras().getString("url"));

			VideoView video = (VideoView) findViewById(R.id.videoView1);
			MediaController mc = new MediaController(this);
			mc.setAnchorView(video);
			mc.setMediaPlayer(video);
			// Uri video = Uri.parse(LINK);
			video.setMediaController(mc);
			video.setVideoURI(uri);
			video.start();
		}
	}

}
