package com.sequencing.fileselector.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sequencing.fileselector.activity.SplashActivity;
import com.sequencing.oauth.core.SequencingOAuth2Client;
import com.sequencing.oauth.exception.NonAuthorizedException;
import com.sequencing.fileselector.activity.FileSelectorActivity;

import java.io.Serializable;

/**
 * Class determines basic action in relation to Sequencing.com
 */
public class SQUIFileSelectHandler {

    /**
     * App context
     */
    private Context context;

    /**
     * User callback for handling selected file
     */
    private static ISQFileCallback fileCallback;

    /**
     * oAuth2Client instance
     */
    private static SequencingOAuth2Client sequencingOAuth2Client;

    private static final String TAG = "SQUIFileSelectHandler";

    public SQUIFileSelectHandler(Context context){
        this.context = context;
    }

    /**
     * Receives files from Sequencing.com and sends them to user handler
     * @param fileCallback user callback for handling selected file
     * @param fileId previous selected file
     */
    public void selectFile(SequencingOAuth2Client sequencingOAuth2Client, ISQFileCallback fileCallback, boolean showRotatingCube, @Nullable String fileId){
        if (fileCallback == null)
            throw new RuntimeException();
        this.fileCallback = fileCallback;
        this.sequencingOAuth2Client = sequencingOAuth2Client;

        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra("fileId", fileId);
        intent.putExtra("showRotatingCube", showRotatingCube);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static ISQFileCallback getFileCallback(){
        return fileCallback;
    }


    public static SequencingOAuth2Client getSequencingOAuth2Client() {
        return sequencingOAuth2Client;
    }
}
