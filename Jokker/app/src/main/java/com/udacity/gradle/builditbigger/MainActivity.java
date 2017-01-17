package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.udacity.gradle.builditbigger.jokeui.JokeDetailActivity;
import com.udacity.gradle.builtitbigger.Joke;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Joke joke = Joke.getRandomJoke();
        if (joke != null && !TextUtils.isEmpty(joke.getContent())) {
            Intent intent = new Intent(this, JokeDetailActivity.class);
            intent.putExtra(JokeDetailActivity.JOKE_EXTRA_TRANSFER, joke);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.toast_joke_failed_empty, Toast.LENGTH_SHORT).show();
        }
    }

}
