package gr.alfoks.popularmovies.mvp.model;

import java.util.Date;

public class MovieBuilder {
    private long id;
    private String title;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private float voteAverage;
    private Date releaseDate;
    private int runtime;
    private boolean favorite = false;

    public MovieBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public MovieBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public MovieBuilder setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
        return this;
    }

    public MovieBuilder setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public MovieBuilder setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public MovieBuilder setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    public MovieBuilder setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public MovieBuilder setRuntime(int runtime) {
        this.runtime = runtime;
        return this;
    }

    public MovieBuilder setFavorite(boolean favorite) {
        this.favorite = favorite;
        return this;
    }

    public Movie build() {
        return new Movie(
            id, title, originalTitle, posterPath, overview, voteAverage,
            releaseDate, runtime, favorite
        );
    }
}
