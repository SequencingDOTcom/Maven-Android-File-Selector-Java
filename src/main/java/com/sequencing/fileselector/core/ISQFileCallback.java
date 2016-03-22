package com.sequencing.fileselector.core;

import android.app.Activity;

import com.sequencing.fileselector.FileEntity;

import java.io.Serializable;


public interface ISQFileCallback {

    /**
     * Callback for handling selected file
     * @param entity selected file entity
     * @param activity activity of file selector
     */
    void onFileSelected(FileEntity entity, Activity activity);
}
