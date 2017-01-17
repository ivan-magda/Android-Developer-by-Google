package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.jokeui.JokeDetailActivity;
import com.udacity.gradle.builditbigger.sync.JokesLoader;
import com.udacity.gradle.builtitbigger.Joke;

import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Joke>> {

    private static final int ID_JOKES_LOADER = 1;

    private View mLoadingView;
    private ProgressBar mProgressBar;
    private boolean mIsNeedShowJoke = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingView = findViewById(R.id.loading_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {
        mIsNeedShowJoke = true;
        triggerLoadingIndicator();
        getSupportLoaderManager().restartLoader(ID_JOKES_LOADER, null, this);
    }

    @Override
    public Loader<List<Joke>> onCreateLoader(int id, Bundle args) {
        return new JokesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Joke>> loader, List<Joke> data) {
        triggerLoadingIndicator();

        if (!mIsNeedShowJoke) return;
        mIsNeedShowJoke = false;

        if (data == null || data.size() == 0) {
            showEmptyJokeToast();
            return;
        }

        int index = new Random().nextInt(data.size());
        Joke joke = data.get(index);
        if (joke != null && !TextUtils.isEmpty(joke.getContent())) {
            Intent intent = new Intent(this, JokeDetailActivity.class);
            intent.putExtra(JokeDetailActivity.JOKE_EXTRA_TRANSFER, joke);
            startActivity(intent);
        } else {
            showEmptyJokeToast();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Joke>> loader) {
    }

    private void triggerLoadingIndicator() {
        if (mIsNeedShowJoke) {
            mLoadingView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    private void showEmptyJokeToast() {
        Toast.makeText(this, R.string.toast_joke_failed_empty, Toast.LENGTH_SHORT).show();
    }

}
