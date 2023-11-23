package com.example.javacustomexoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class ExoPlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    Boolean fullScreen = false;


    public DefaultTrackSelector trackSelector;
    SimpleExoPlayer simpleExoPlayer;
    public String VideoUrl1 = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4";
    public String VideoUrl2 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SCALED, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //        hide notch and fill full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.activity_exo_player);

        trackSelector = new DefaultTrackSelector(ExoPlayerActivity.this);
        TrackSelectionParameters newParameters = trackSelector.getParameters()
                .buildUpon()
                .setForceLowestBitrate(true)
                .build();
        trackSelector.setParameters((DefaultTrackSelector.Parameters) newParameters);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();

        MediaItem mediaItem1 = MediaItem.fromUri(VideoUrl1);
        MediaItem mediaItem2 = MediaItem.fromUri(VideoUrl2);

        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        mediaItems.add(mediaItem1);
        mediaItems.add(mediaItem2);

//        Fetching all the id of xml file
        playerView = findViewById(R.id.pvExoPlayer);
        playerView.setPlayer(simpleExoPlayer);

        simpleExoPlayer.addMediaItems(mediaItems);
        simpleExoPlayer.prepare();
        simpleExoPlayer.play();

        ImageButton ibBack = findViewById(R.id.ibBack);
        ImageButton ibPlay = findViewById(R.id.ibPlay);
        ImageButton ibPause =  findViewById(R.id.ibPause);
        ImageButton ibForward10Sec = findViewById(R.id.ibForward10Sec);
        ImageButton ibBackward10Sec = findViewById(R.id.ibBackward10Sec);
        ImageButton ibFullScreen = playerView.findViewById(R.id.ibFullScreen);
        ImageButton ibFullScreenExit = findViewById(R.id.ibFullScreenExit);
        ImageButton ibSetting = findViewById(R.id.ibSetting);
        TextView tvStartTimer = findViewById(R.id.tvStartTimer);
        TextView tvDurationTImer = findViewById(R.id.tvDurationTimer);


//        Customs Exo player buttons

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibForward10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
            }
        });

        ibBackward10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long num = simpleExoPlayer.getCurrentPosition() - 10000;
                if (num < 0){
                    simpleExoPlayer.seekTo(0);
                } else {
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
                }
            }
        });

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.play();
                ibPause.setVisibility(View.VISIBLE);
                ibPlay.setVisibility(View.GONE);
            }
        });

        ibPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.pause();
                ibPause.setVisibility(View.GONE);
                ibPlay.setVisibility(View.VISIBLE);
            }
        });



        ibFullScreen.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override

                public void onClick (View view){

                if(fullScreen) {
                    ibFullScreen.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_fullscreen));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullScreen = false;
                }else{
                    ibFullScreen.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_fullscreen_exit));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullScreen = true;
                }

//                int orientation = ExoPlayerActivity.this.getResources().getConfiguration().orientation;
//                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    fullScreen = true;
//                    ibFullScreen.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_fullscreen_exit));
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    playerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                } else {
//                    fullScreen = false;
//                    ibFullScreen.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_fullscreen));
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    playerView.getLayoutParams().height = 870;
                }


        });






    }

//     Implement the checkLandscapeOrientation and changeOrientationToLandscape methods here
//
//     For example:




    @Override
    protected void onResume() {
        super.onResume();
        simpleExoPlayer.seekToDefaultPosition();
        simpleExoPlayer.setPlayWhenReady(true);
        ImageButton ibPlay = findViewById(R.id.ibPlay);
        ImageButton ibPause =  findViewById(R.id.ibPause);
        ibPause.setVisibility(View.VISIBLE);
        ibPlay.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (simpleExoPlayer != null){
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        simpleExoPlayer.release();
        super.onDestroy();
    }
}