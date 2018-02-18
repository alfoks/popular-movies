package gr.alfoks.popularmovies.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Movies {
    @SerializedName("results")
    private final List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public Movies(List<Movie> movies){
        this.movies = movies;
    }
}
