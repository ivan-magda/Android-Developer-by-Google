package com.udacity.gradle.builditbigger.backend;

import com.udacity.gradle.builtitbigger.Joke;

/**
 * The object model for the data we are sending through endpoints
 */
public class JokesBean {

    private String[] myData;

    public String[] getData() {
        return myData;
    }

    public void setData(String[] data) {
        myData = data;
    }

    public void setJokes(Joke[] jokes) {
        if (jokes != null && jokes.length > 0) {
            String[] jokesAsString = new String[jokes.length];
            for (int i = 0; i < jokes.length; i++) {
                jokesAsString[i] = jokes[i].getContent();
            }
            myData = jokesAsString;
        } else {
            myData = null;
        }
    }

}
