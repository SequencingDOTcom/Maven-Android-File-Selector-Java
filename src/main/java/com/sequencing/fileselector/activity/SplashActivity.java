package com.sequencing.fileselector.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.sequencing.fileselector.R;
import com.sequencing.fileselector.core.FileSelectorParameters;
import com.sequencing.fileselector.core.SQUIFileSelectHandler;
import com.sequencing.oauth.exception.NonAuthorizedException;



public class SplashActivity extends AppCompatActivity {

    private RequestDateTask requestDateTask;
    private ProgressBar pbProgress;

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);

        if(!getIntent().getBooleanExtra("showRotatingCube", true))
            pbProgress.setVisibility(View.GONE);

        requestDateTask = new RequestDateTask();
        requestDateTask.execute();
    }

    class RequestDateTask extends AsyncTask<Void, Void, Void> {
        private String jsonResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                jsonResponse = FileSelectorParameters.getInstance().getFilesApi(SQUIFileSelectHandler.getSequencingOAuth2Client()).getFiles();
            } catch (NonAuthorizedException e) {
                Log.w(TAG, "Non authorized user", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Intent intent = new Intent(getBaseContext(), PreFileSelectorActivity.class);
            intent.putExtra("serverResponse", jsonResponse);
            intent.putExtra("fileId", getIntent().getStringExtra("fileId"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getBaseContext().startActivity(intent);
            finish();
        }
    }
}
