package com.sequencing.fileselector.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.sequencing.fileselector.R;
import com.sequencing.fileselector.videobackground.customview.CVideoView;
import com.sequencing.fileselector.videobackground.helper.AppUIHelper;

public class PreFileSelectorActivity extends AppCompatActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {

    private static final String TAG = "PreFileSelectorActivity";
    private Intent fileSelectorIntent;
    private FloatingActionButton fabInfo;
    private Toolbar toolbar;
    private Button btnMyFiles;
    private Button btnSampleFiles;
    private ToolTipView toolTipView;
    private CVideoView videoView;
    private String videoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_file_selector);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.fs_colorAccent));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Drawable drawableMyFiles = getResources().getDrawable(R.drawable.my_files_icon_sequencing_com_color);
        drawableMyFiles.setBounds(0, 0, (int)(drawableMyFiles.getIntrinsicWidth()*0.65), (int)(drawableMyFiles.getIntrinsicHeight()*0.65));
        ScaleDrawable sdMyFiles = new ScaleDrawable(drawableMyFiles, 0, 50, 50);

        Drawable drawableSampleFiles = getResources().getDrawable(R.drawable.sample_files_icon_sequencing_com_blue);
        drawableSampleFiles.setBounds(0, 0, (int) (drawableSampleFiles.getIntrinsicWidth() * 0.6), (int) (drawableSampleFiles.getIntrinsicHeight() * 0.6));
        ScaleDrawable sdSampleFiles = new ScaleDrawable(drawableSampleFiles, 0, 50, 50);



        videoView = (CVideoView) findViewById(R.id.video_view);
        btnMyFiles = (Button) findViewById(R.id.btnMyFiles);
        btnMyFiles.setCompoundDrawables(sdMyFiles.getDrawable(), null, null, null);

        btnSampleFiles = (Button) findViewById(R.id.btnSampleFiles);
        btnSampleFiles.setCompoundDrawables(sdSampleFiles.getDrawable(), null, null, null);

        btnMyFiles.setOnClickListener(this);
        btnSampleFiles.setOnClickListener(this);

        fabInfo = (FloatingActionButton) findViewById(R.id.fab);
        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toolTipView != null){
                    toolTipView.remove();
                    toolTipView = null;
                } else {
                    displayInfo();
                }
            }
        });

        fileSelectorIntent = new Intent(getBaseContext(), FileSelectorActivity.class);
        fileSelectorIntent.putExtra("serverResponse", getIntent().getStringExtra("serverResponse"));
        fileSelectorIntent.putExtra("fileId", getIntent().getStringExtra("fileId"));
        fileSelectorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoName = getIntent().getStringExtra("videoName");
        if(videoName != null){
            initVideoView();
            playVideo();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnMyFiles) {
            fileSelectorIntent.putExtra("tab", "my_files");
            startActivity(fileSelectorIntent);
            finish();
        } else if (i == R.id.btnSampleFiles) {
            fileSelectorIntent.putExtra("tab", "sample_files");
            startActivity(fileSelectorIntent);
            finish();
        }
    }

    private void displayInfo(){
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.tooltip);

        ToolTip toolTip = new ToolTip()
                .withText(R.string.file_selector_info)
                .withColor(Color.GRAY)
                .withTextColor(Color.WHITE)
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        toolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, fabInfo);
        toolTipView.setOnToolTipViewClickedListener(this);
    }

    /**
     * Handles ToolTipViews
     * @param toolTipView pressed ToolTipView
     */
    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
        this.toolTipView.remove();
        this.toolTipView = null;
    }

    private void initVideoView() {
        if (videoView != null) {
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    videoView.setOnCorrectVideoDimensions(getCorrectVideoDimensions());
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            videoView.onVideoSizeChanged(mp);
                        }
                    });
                }
            });
        }
    }

    private void playVideo() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            videoView.setAlignment(CVideoView.ALIGN_WIDTH);
        } else {
            videoView.setAlignment(CVideoView.ALIGN_NONE);
        }
        videoView.setVisibility(View.VISIBLE);
        String videoNameWithoutExtension = videoName.split("\\.")[0];
        int raw_id = getResources().getIdentifier(videoNameWithoutExtension, "raw", getPackageName());
        String path = "android.resource://" + getPackageName() + "/" + raw_id;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
    }

    private void stopVideo() {
        videoView.stopPlayback();
        videoView.setVisibility(View.GONE);
    }

    /**
     * Adjust video layout according to video dimensions
     */
    private CVideoView.OnCorrectVideoDimensions getCorrectVideoDimensions() {
        return new CVideoView.OnCorrectVideoDimensions() {
            @Override
            public void correctDimensions(int width, int height) {
                AppUIHelper.resizeLayout(videoView, height, width, 300, null);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if(videoView != null){
            stopVideo();
        }
    }
}
