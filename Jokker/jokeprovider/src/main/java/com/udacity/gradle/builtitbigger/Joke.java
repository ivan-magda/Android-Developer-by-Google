package com.udacity.gradle.builtitbigger;

public class Joke {

    private String mContent;

    public Joke(String content) {
        this.mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public static Joke[] getJokes() {
        return new Joke[]{
                new Joke("I dreamt I was forced to eat a giant marshmallow. When I woke up, my pillow was gone."),
                new Joke("Two Elephants meet a totally naked guy. After a while one elephant says to the other: “I really don’t get how he can feed himself with that thing!"),
                new Joke("My dog used to chase people on a bike a lot. It got so bad, finally I had to take his bike away.")
        };
    }

}
