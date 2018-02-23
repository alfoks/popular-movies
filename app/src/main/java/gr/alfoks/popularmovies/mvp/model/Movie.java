package gr.alfoks.popularmovies.mvp.model;

import java.util.Date;

public class Movie {
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public final String title;
    public final String original_title;
    public final String poster_path;
    public final String overview;
    public final float vote_average;
    public final Date release_date;

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

    public String getFullPosterPath() {
        return POSTER_BASE_URL + poster_path;
    }
}
