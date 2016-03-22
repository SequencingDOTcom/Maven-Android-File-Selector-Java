package com.sequencing.fileselector.core;

import com.sequencing.oauth.core.DefaultSequencingFileMetadataApi;
import com.sequencing.oauth.core.SequencingFileMetadataApi;
import com.sequencing.oauth.core.SequencingOAuth2Client;

/**
 * Definition parameters for Sequencing  files API
 */
public class FileSelectorParameters {

    /**
     * Determine methods for receiving files data
     */
    private SequencingFileMetadataApi filesApi;

    private static final FileSelectorParameters instance = new FileSelectorParameters();

    private FileSelectorParameters(){}

    public static FileSelectorParameters getInstance(){
        return instance;
    }

    public SequencingFileMetadataApi getFilesApi(SequencingOAuth2Client sequencingOAuth2Client){
        if (filesApi == null){
            filesApi = new DefaultSequencingFileMetadataApi(sequencingOAuth2Client);
        }
        return filesApi;
    }
}
