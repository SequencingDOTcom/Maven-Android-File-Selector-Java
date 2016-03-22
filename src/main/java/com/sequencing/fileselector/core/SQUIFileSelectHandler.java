package com.sequencing.fileselector.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

    private static final String TAG = "SQUIFileSelectHandler";

    public SQUIFileSelectHandler(Context context){
        this.context = context;
    }

    /**
     * Receives files from Sequencing.com and sends them to user handler
     * @param fileCallback user callback for handling selected file
     */
    public void selectFile(SequencingOAuth2Client sequencingOAuth2Client, ISQFileCallback fileCallback){
        if (fileCallback == null)
            throw new RuntimeException();
        this.fileCallback = fileCallback;

        String jsonResponse = null;
        try {
            jsonResponse = FileSelectorParameters.getInstance().getFilesApi(sequencingOAuth2Client).getFiles();
        } catch (NonAuthorizedException e) {
            Log.w(TAG, "Non authorized user", e);
        }

        Intent intent = new Intent(context, FileSelectorActivity.class);
        intent.putExtra("serverResponse", jsonResponse);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static ISQFileCallback getFileCallback(){
        return fileCallback;
    }
}
