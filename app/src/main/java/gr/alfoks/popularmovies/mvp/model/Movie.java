package gr.alfoks.popularmovies.mvp.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Movie implements Serializable {
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public final long id;
    public final String title;
    @SerializedName("original_title")
    public final String originalTitle;
    @SerializedName("poster_path")
    public final String posterPath;
    public final String overview;
    @SerializedName("vote_average")
    public final float voteAverage;
    @SerializedName("release_date")
    public final Date releaseDate;
    public final int runtime;

    public Movie(
        long id,
        String title,
        String originalTitle,
        String posterPath,
        String overview,
        float voteAverage,
        Date releaseDate,
        int runtime
    ) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
    }

    public String getFullPosterPath() {
        return POSTER_BASE_URL + posterPath;
    }

    public int getReleaseYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(releaseDate);
        return calendar.get(Calendar.YEAR);
    }

    public String getDuration() {
        int hours = runtime / 60;
        int minutes = runtime - (hours * 60);

        StringBuilder sb = new StringBuilder();
        if(hours > 0) {
            sb.append(hours).append(":");
        }
        if(minutes > 0) {
            sb.append(minutes);
        }

        return sb.toString();
    }

    public String getRating() {
        return voteAverage + "/10";
    }
}
