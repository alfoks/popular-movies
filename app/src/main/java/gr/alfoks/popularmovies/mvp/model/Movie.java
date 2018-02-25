package gr.alfoks.popularmovies.mvp.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Movie implements Serializable {
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public final long id;
    public final String title;
    public final String original_title;
    public final String poster_path;
    public final String overview;
    public final float vote_average;
    public final Date release_date;
    public final int runtime;

    public Movie(
        long id,
        String title,
        String original_title,
        String poster_path,
        String overview,
        float vote_average,
        Date release_date,
        int runtime
    ) {
        this.id = id;
        this.title = title;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.runtime = runtime;
    }

    public String getFullPosterPath() {
        return POSTER_BASE_URL + poster_path;
    }

    public int getReleaseYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(release_date);
        return calendar.get(Calendar.YEAR);
    }

    public String getDuration() {
        int hours = runtime / 60;
        int minutes = runtime - (hours * 60);

        StringBuilder sb = new StringBuilder();
        if(hours > 0) {
            sb.append(hours).append("h").append(" ");
        }
        if(minutes > 0) {
            sb.append(minutes).append("min");
        }

        return sb.toString();
    }

    public String getRating() {
        return vote_average + "/10";
    }
}
