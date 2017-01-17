package com.udacity.gradle.builditbigger.sync;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;
import com.udacity.gradle.builditbigger.backend.myApi.model.JokesBean;
import com.udacity.gradle.builtitbigger.Joke;
import com.udacity.gradle.builtitbigger.JokeUtils;

import java.io.IOException;
import java.util.List;

public final class JokesLoader extends AsyncTaskLoader<List<Joke>> {

    private static final String TAG = JokesLoader.class.getSimpleName();

    private MyApi mMyApiService = null;

    public JokesLoader(Context context) {
        super(context);

        MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                        request.setDisableGZipContent(true);
                    }
                });
        mMyApiService = builder.build();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Joke> loadInBackground() {
        try {
            JokesBean response = mMyApiService.getJokes().execute();
            return JokeUtils.fromStrings(response.getData());
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch jokes with error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
