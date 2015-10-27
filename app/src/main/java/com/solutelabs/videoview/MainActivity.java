package com.solutelabs.videoview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.solutelabs.VideoView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setSource(R.raw.sample);
    }
}
