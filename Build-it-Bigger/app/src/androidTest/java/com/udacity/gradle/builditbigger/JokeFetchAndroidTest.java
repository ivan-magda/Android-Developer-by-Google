package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.gradle.builditbigger.sync.JokesTask;
import com.udacity.gradle.builtitbigger.Joke;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class JokeFetchAndroidTest {

    @Test
    public void testVerifyJokeFetchingResponse() {
        AsyncTask<Void, Void, List<Joke>> asyncTask = new AsyncTask<Void, Void, List<Joke>>() {
            @Override
            protected List<Joke> doInBackground(Void... params) {
                return new JokesTask().getJokes();
            }

            @Override
            protected void onPostExecute(List<Joke> jokes) {
                super.onPostExecute(jokes);
                Assert.assertTrue(jokes != null && jokes.size() > 0);
            }
        };

        asyncTask.execute();
    }

}
