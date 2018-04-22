package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.api.TheMovieDbApi;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.Reviews;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;

import android.support.annotation.NonNull;

public class TheMovieDbDataSource implements MoviesDataSource {
    @NonNull
    private final TheMovieDbApi theMovieDbApi;

    public TheMovieDbDataSource(@NonNull TheMovieDbApi theMovieDbApi) {
        this.theMovieDbApi = theMovieDbApi;
    }

    @Override
    public Single<Movie> loadMovie(long movieId) {
        return theMovieDbApi.loadMovie(movieId);
    }

    @Override
    public Single<Movies> loadMovies(SortBy sortBy, int page) {
        return theMovieDbApi.loadMovies(sortBy, page);
    }

    @Override
    public Single<Reviews> loadReviews(long movieId, int page) {
        return theMovieDbApi.loadReviews(movieId, page);
    }
}
