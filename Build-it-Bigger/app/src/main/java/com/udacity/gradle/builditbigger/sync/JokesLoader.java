package com.udacity.gradle.builditbigger.sync;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.udacity.gradle.builtitbigger.Joke;

import java.util.List;

public final class JokesLoader extends AsyncTaskLoader<List<Joke>> {

    private static final String TAG = JokesLoader.class.getSimpleName();

    public JokesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Joke> loadInBackground() {
        return new JokesTask().getJokes();
    }

}
