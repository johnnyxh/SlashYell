package com.slashyell.jxhernandez.slashyell;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApi;
import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

/**
 * Created by johnny on 4/28/15.
 */
public class GetYells extends AsyncTask<GeoPt, Void, List<YellMessage>> {

    MessageReceiver context;

    public GetYells(MessageReceiver context) {
        this.context = context;
    }

    @Override
    protected List<YellMessage> doInBackground(GeoPt... params) {
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
            return myApi.getAround(params[0].getLatitude(), params[0].getLongitude()).execute().getItems();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<YellMessage> messages) {
        context.onMessagesReceived(messages);
    }
}
