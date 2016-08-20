package com.awesomekris.android.app.popularmovies;

/**
 * Created by kris on 16/8/19.
 */
public class Review {
    public Review(){}

    private String name;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
