package com.example.javacustomexoplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Timeline;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ExoPlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    Boolean fullScreen = false;
    Boolean fillMode = false;

    float speed;
    PlaybackParameters parameters;


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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

        screenOrientation();

//        Fetching all the id of xml file
        playerView = findViewById(R.id.pvExoPlayer);
        playerView.setPlayer(simpleExoPlayer);
        simpleExoPlayer.setPlaybackParameters(parameters);
        simpleExoPlayer.addMediaItems(mediaItems);
//        simpleExoPlayer.seekTo()
        simpleExoPlayer.prepare();
        simpleExoPlayer.play();


        ImageButton ibBack = findViewById(R.id.ibBack);
        ImageButton ibPlay = findViewById(R.id.ibPlay);
        ImageButton ibPause = findViewById(R.id.ibPause);
        ImageButton ibForward10Sec = findViewById(R.id.ibForward10Sec);
        ImageButton ibBackward10Sec = findViewById(R.id.ibBackward10Sec);
        ImageButton ibFullScreen = playerView.findViewById(R.id.ibFullScreen);
        ImageButton ibResizeScreen1 = findViewById(R.id.ibResizeScreen1);
        ImageButton ibResizeScreen2 = findViewById(R.id.ibResizeScreen2);
        ImageButton ibResizeScreen3 = findViewById(R.id.ibResizeScreen3);
        ImageButton ibResizeScreen4 = findViewById(R.id.ibResizeScreen4);
        ImageButton ibResizeScreen5 = findViewById(R.id.ibResizeScreen5);
        ImageButton ibFullScreenExit = playerView.findViewById(R.id.ibFullScreenExit);
        ImageButton ibSetting = playerView.findViewById(R.id.ibSetting);
        TextView tvStartTimer = playerView.findViewById(R.id.tvStartTimer);
        TextView tvDurationTImer = playerView.findViewById(R.id.tvDurationTimer);


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
                if (num < 0) {
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
                tvDurationTImer.setText(String.valueOf(getVideoDurationSeconds(simpleExoPlayer)));
                tvStartTimer.setText(String.valueOf(getCurrentVideoDuration(simpleExoPlayer)));


//                Toast.makeText(ExoPlayerActivity.this, getVideoDurationSeconds(simpleExoPlayer), Toast.LENGTH_SHORT).show();
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
            public void onClick(View view) {

                if (fullScreen) {
                    ibFullScreen.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_fullscreen));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
//                    params.height = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullScreen = false;
                } else {
                    ibFullScreen.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_fullscreen_exit));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullScreen = true;
                }
            }
        });
// ================ SETTING FOR SPEED AND QUALITY THROUGH POP UP MENU BUT NOT WORKING
//        ibSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popupMenu = new PopupMenu(ExoPlayerActivity.this, ibSetting, Gravity.FILL);
//
//
//                popupMenu.getMenuInflater().inflate(R.menu.setting_for_exoplayer, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//
//                        Toast.makeText(ExoPlayerActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
//                        return true;
////                        if (menuItem.getItemId() == R.id.epvSpeed){
////
////                        }
//                    }
//                });
//                popupMenu.show();
//            }
//        });

        // ================ SETTING AND QUALITY THROUGH ALERT DIALOG BOX
        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ExoPlayerActivity.this);
                alertDialog.setPositiveButton("OK", null);
                String[] options = {"Speed", "Quality"};
                int clickedItem = -1;
                alertDialog.setSingleChoiceItems(options, clickedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ExoPlayerActivity.this);
                                alertDialog.setTitle("Select Playback Speed").setPositiveButton("OK", null);
                                String[] items = {"0.5x", "1x Normal Speed", "1.25x", "1.5x", "2x"};
                                int checkedItem = -1; // We do not want any item to be selected as default that's why -1
                                alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                speed = 0.5f;
                                                parameters = new PlaybackParameters(speed);
                                                simpleExoPlayer.setPlaybackParameters(parameters);
                                                break;
                                            case 1:
                                                speed = 1f;
                                                parameters = new PlaybackParameters(speed);
                                                simpleExoPlayer.setPlaybackParameters(parameters);
                                                break;
                                            case 2:
                                                speed = 1.25f;
                                                parameters = new PlaybackParameters(speed);
                                                simpleExoPlayer.setPlaybackParameters(parameters);
                                                break;
                                            case 3:
                                                speed = 1.5f;
                                                parameters = new PlaybackParameters(speed);
                                                simpleExoPlayer.setPlaybackParameters(parameters);
                                                break;
                                            case 4:
                                                speed = 2f;
                                                parameters = new PlaybackParameters(speed);
                                                simpleExoPlayer.setPlaybackParameters(parameters);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                                AlertDialog alert = alertDialog.create();
                                alert.show();

                                break;

                            case 1:
//                          THIS WILL BE FOR VIDEO QUALITY OPTION

                                break;

                            default:
                                break;
                        }

                    }
                });
                AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.show();
            }
        });

//    ===== Setting in exo player 3 only for speed =====
//        ibSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ExoPlayerActivity.this);
//                alertDialog.setTitle("Select Playback Speed").setPositiveButton("OK", null);
//                String[] items = {"0.5x","1x Normal Speed","1.25x","1.5x","2x"};
//                int checkedItem = -1; // We do not want any item to be selected as default that's why -1
//                alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case 0:
//                                speed = 0.5f;
//                                parameters = new PlaybackParameters(speed);
//                                simpleExoPlayer.setPlaybackParameters(parameters);
//                                break;
//                            case 1:
//                                speed = 1f;
//                                parameters = new PlaybackParameters(speed);
//                                simpleExoPlayer.setPlaybackParameters(parameters);
//                                break;
//                            case 2:
//                                speed = 1.25f;
//                                parameters = new PlaybackParameters(speed);
//                                simpleExoPlayer.setPlaybackParameters(parameters);
//                                break;
//                            case 3:
//                                speed = 1.5f;
//                                parameters = new PlaybackParameters(speed);
//                                simpleExoPlayer.setPlaybackParameters(parameters);
//                                break;
//                            case 4:
//                                speed = 2f;
//                                parameters = new PlaybackParameters(speed);
//                                simpleExoPlayer.setPlaybackParameters(parameters);
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                });
//                AlertDialog alert = alertDialog.create();
//                alert.show();
//
//            }
//        });

        ibResizeScreen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ibResizeScreen1.setVisibility(View.VISIBLE);
                ibResizeScreen2.setVisibility(View.GONE);
                ibResizeScreen3.setVisibility(View.GONE);
                ibResizeScreen4.setVisibility(View.GONE);
                ibResizeScreen5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                ibResizeScreen1.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_minimize_aspect_ratio));

//   ========== HAVE TO WORK ON DISABLE FILL MODE ==========
//                if (fillMode){
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                    ibResizeScreen1.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.aspect_ratio));
//                    fillMode = false;
//
//                } else {
//                    fillMode = true;
//                }

            }
        });


        ibResizeScreen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibResizeScreen1.setVisibility(View.GONE);
                ibResizeScreen2.setVisibility(View.VISIBLE);
                ibResizeScreen3.setVisibility(View.GONE);
                ibResizeScreen4.setVisibility(View.GONE);
                ibResizeScreen5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                ibResizeScreen2.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_minimize_aspect_ratio));

//   ========== HAVE TO WORK ON DISABLE FILL MODE ==========
//                if (fillMode){
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                    ibResizeScreen2.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.aspect_ratio));
//                    fillMode = false;
//
//                } else {
//                          fillMode = true;
//                    Toast.makeText(ExoPlayerActivity.this, "FILL MODE", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        ibResizeScreen3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibResizeScreen1.setVisibility(View.GONE);
                ibResizeScreen2.setVisibility(View.GONE);
                ibResizeScreen3.setVisibility(View.VISIBLE);
                ibResizeScreen4.setVisibility(View.GONE);
                ibResizeScreen5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);

                ibResizeScreen3.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_minimize_aspect_ratio));

//   ========== HAVE TO WORK ON DISABLE FILL MODE ==========
//                if (fillMode){
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                    ibResizeScreen3.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.aspect_ratio));
//                    fillMode = false;
//
//                } else {
//             fillMode = true;
//                    Toast.makeText(ExoPlayerActivity.this, "HEIGHT MODE", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        ibResizeScreen4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibResizeScreen1.setVisibility(View.GONE);
                ibResizeScreen2.setVisibility(View.GONE);
                ibResizeScreen3.setVisibility(View.GONE);
                ibResizeScreen4.setVisibility(View.VISIBLE);
                ibResizeScreen5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                ibResizeScreen4.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_minimize_aspect_ratio));

//   ========== HAVE TO WORK ON DISABLE FILL MODE ==========
//                if (fillMode){
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                    ibResizeScreen4.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.aspect_ratio));
//                    fillMode = false;
//
//                } else {
//        fillMode = true;
//                    Toast.makeText(ExoPlayerActivity.this, "WIDTH MODE", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        ibResizeScreen5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibResizeScreen1.setVisibility(View.GONE);
                ibResizeScreen2.setVisibility(View.GONE);
                ibResizeScreen3.setVisibility(View.GONE);
                ibResizeScreen4.setVisibility(View.GONE);
                ibResizeScreen5.setVisibility(View.VISIBLE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                ibResizeScreen5.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.baseline_minimize_aspect_ratio));

//   ========== HAVE TO WORK ON DISABLE FILL MODE ==========
//                if (fillMode){
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                    ibResizeScreen5.setImageDrawable(ContextCompat.getDrawable(ExoPlayerActivity.this, R.drawable.aspect_ratio));
//                    fillMode = false;
//
//                } else {
//               fillMode = true;
//                    Toast.makeText(ExoPlayerActivity.this, "FIT MODE", Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }

    private void screenOrientation() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bitmap;
            String path = VideoUrl1;
            Uri uri = Uri.parse(path);
            retriever.setDataSource(this, uri);
            bitmap = retriever.getFrameAtTime();

            int videoWidth = bitmap.getWidth();
            int videoHeight = bitmap.getHeight();
            if (videoWidth > videoHeight) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (Exception e) {
            Log.e("MediaMetaDataRetriever", "screenOrientation: ");
        }
    }


    private int getVideoDurationSeconds(SimpleExoPlayer player) {
        int timeMs = (int) player.getDuration();
        int totalSeconds = timeMs / 1000;
        return totalSeconds;
    }

    private int getCurrentVideoDuration(SimpleExoPlayer player) {
        int timeMs = (int) player.getCurrentPosition();
        int totalSeconds = timeMs / 1000;
        return totalSeconds;
    }


    @Override
    protected void onResume() {
        super.onResume();
        simpleExoPlayer.seekToDefaultPosition();
        simpleExoPlayer.setPlayWhenReady(true);
        ImageButton ibPlay = findViewById(R.id.ibPlay);
        ImageButton ibPause = findViewById(R.id.ibPause);
        ibPause.setVisibility(View.VISIBLE);
        ibPlay.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        simpleExoPlayer.release();
        super.onDestroy();
    }
}