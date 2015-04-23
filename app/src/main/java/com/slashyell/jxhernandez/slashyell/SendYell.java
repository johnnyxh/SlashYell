package com.slashyell.jxhernandez.slashyell;

import android.location.LocationManager;
import android.os.AsyncTask;

import com.example.johnny.myapplication.backend.yellMessageApi.YellMessageApi;
import com.example.johnny.myapplication.backend.yellMessageApi.model.GeoPt;
import com.example.johnny.myapplication.backend.yellMessageApi.model.YellMessage;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by scien_000 on 4/23/2015.
 */
public class SendYell extends AsyncTask<YellMessage, Void, Void>
{

    @Override
    protected Void doInBackground(YellMessage... message) {
        YellMessageApi.Builder builder = new YellMessageApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });

        YellMessageApi myApi = builder.build();

        try {
            myApi.insert(message[0]).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
