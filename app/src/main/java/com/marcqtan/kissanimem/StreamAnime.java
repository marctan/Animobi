
package com.marcqtan.kissanimem;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class StreamAnime extends AppCompatActivity {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;

    // Insert your Video URL
    //String VideoURL = "https://3.bp.blogspot.com/JYQh2jnE95Y8Lw7M0tpLDWSdj1lX5UYwsL7dP-S2UDRalItZq6wK5euZ-9ok2BQOyOYNjzY4uw=m22";

    //String VideoURL = "https://streamango.com/embed/olskketknpaasasr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from video_main.xml
        setContentView(R.layout.activity_stream_anime);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Find your VideoView in your video_main.xml layout
        videoview = (VideoView) findViewById(R.id.VideoView);
        // Execute StreamVideo AsyncTask

        // Create a progressbar
        pDialog = new ProgressDialog(StreamAnime.this);
        // Set progressbar title
        pDialog.setTitle(getIntent().getStringExtra("episodeName") + " " + getIntent().getStringExtra("animeName"));
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();
        String vidurl = getIntent().getStringExtra("vidurl");

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    StreamAnime.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(vidurl);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            pDialog.dismiss();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

    }

}