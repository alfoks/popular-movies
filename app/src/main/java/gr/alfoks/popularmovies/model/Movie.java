package gr.alfoks.popularmovies.model;

import java.util.Date;

public class Movie {
    private final String title;
    private final String original_title;
    private final String poster_path;
    private final String overview;
    private final float vote_average;
    private final Date release_date;

    public Movie(
        String title,
        String original_title,
        String poster_path,
        String overview,
        float vote_average,
        Date release_date
    ) {
        this.title = title;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }
}
