/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.example.quizexample;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.udacity.example.droidtermsprovider.DroidTermsExampleContract;

/**
 * Gets the data from the ContentProvider and shows a series of flash cards.
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // The current state of the app
    private int mCurrentState;
    private Button mButton;

    // This state is when the word definition is hidden and clicking the button will therefore
    // show the definition
    private final int STATE_HIDDEN = 0;

    // This state is when the word definition is shown and clicking the button will therefore
    // advance the app to the next word
    private final int STATE_SHOWN = 1;

    // The data from the DroidTermsExample content provider.
    private Cursor mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button_next);
        new DroidTermsTask().execute();
    }

    /**
     * This is called from the layout when the button is clicked and switches between the
     * two app states.
     *
     * @param view The view that was clicked
     */
    public void onButtonClick(View view) {
        // Either show the definition of the current word, or if the definition is currently
        // showing, move to the next word.
        switch (mCurrentState) {
            case STATE_HIDDEN:
                showDefinition();
                break;
            case STATE_SHOWN:
                nextWord();
                break;
        }
    }

    public void nextWord() {
        mButton.setText(getString(R.string.show_definition));
        mCurrentState = STATE_HIDDEN;
    }

    public void showDefinition() {
        mButton.setText(getString(R.string.next_word));
        mCurrentState = STATE_SHOWN;
    }

    final class DroidTermsTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            ContentResolver contentResolver = getContentResolver();
            return contentResolver.query(DroidTermsExampleContract.CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            mData = cursor;

            final int NOT_FOUND = -1;
            int wordColumn = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_WORD);
            int definitionColumn = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_DEFINITION);

            if (wordColumn != NOT_FOUND && definitionColumn != NOT_FOUND) {
                while (cursor.moveToNext()) {
                    String word = mData.getString(wordColumn);
                    String definition = mData.getString(definitionColumn);
                    Log.v(LOG_TAG, word + "-" + definition);
                }
            }
            mData.close();
        }
    }

}
