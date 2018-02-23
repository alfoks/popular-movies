package gr.alfoks.popularmovies.mvp.model;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Movies {
    @SerializedName("results")
    private final List<Movie> movies;

    @SerializedName("total_pages")
    public final int totalPages;

    public List<Movie> getMovies() {
        return Collections.unmodifiableList(movies);
    }

    public Movies(List<Movie> movies, int totalPages) {
        this.movies = movies;
        this.totalPages = totalPages;
    }
}
