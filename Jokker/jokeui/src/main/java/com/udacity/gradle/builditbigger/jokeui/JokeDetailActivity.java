package com.udacity.gradle.builditbigger.jokeui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.udacity.gradle.builtitbigger.Joke;

public class JokeDetailActivity extends AppCompatActivity {

    public static final String JOKE_EXTRA_TRANSFER = "com.udacity.gradle.builditbigger.jokeui.jokeExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_detail);
        configure();
    }

    private void configure() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Joke mJoke;

        if (intent.hasExtra(JOKE_EXTRA_TRANSFER)) {
            mJoke = (Joke) intent.getSerializableExtra(JOKE_EXTRA_TRANSFER);
        } else {
            throw new RuntimeException("Joke must be passed as an intent extra");
        }

        TextView jokeTextView = (TextView) findViewById(R.id.tv_joke);
        if (TextUtils.isEmpty(mJoke.getContent())) {
            TextView emptyTextView = (TextView) findViewById(R.id.tv_joke_empty);
            emptyTextView.setVisibility(View.VISIBLE);
            jokeTextView.setVisibility(View.GONE);
        } else {
            jokeTextView.setText(mJoke.getContent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
