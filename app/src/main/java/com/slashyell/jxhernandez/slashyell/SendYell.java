package com.slashyell.jxhernandez.slashyell;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApi;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by scien_000 on 4/23/2015.
 */
public class SendYell extends AsyncTask<Void, Void, YellMessage>
{

    YellMessage myMessage;
    Activity context;
    boolean closeAfter = true;

    public SendYell(Activity context, YellMessage ym) {
        myMessage = ym;
        this.context = context;
    }
    public SendYell(Activity context, YellMessage ym, boolean close) {
        myMessage = ym;
        closeAfter = close;
        this.context = context;
    }

    @Override
    protected YellMessage doInBackground(Void... params) {
        YellMessageApi.Builder builder = new YellMessageApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl("http://commote.net:8085/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });

        YellMessageApi myApi = builder.build();

        try {
            return myApi.insert(myMessage).execute();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(YellMessage result) {
        if (result != null) {
            Toast.makeText(context, context.getResources().getString(R.string.post_success), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.post_failure), Toast.LENGTH_LONG).show();
        }
        if (closeAfter)
            context.finish();
    }
}
