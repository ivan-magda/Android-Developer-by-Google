package com.udacity.gradle.builtitbigger;

import java.util.ArrayList;
import java.util.List;

public final class JokeUtils {

    private JokeUtils() {
    }

    public static List<Joke> fromStrings(List<String> jokes) {
        if (jokes == null || jokes.size() == 0) return null;
        List<Joke> mappedJokes = new ArrayList<>(jokes.size());
        for (String jokeString : jokes) {
            mappedJokes.add(new Joke(jokeString));
        }
        return mappedJokes;
    }

}
