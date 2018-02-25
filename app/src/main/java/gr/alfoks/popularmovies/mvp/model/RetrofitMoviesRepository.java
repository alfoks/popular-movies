package gr.alfoks.popularmovies.mvp.model;

import gr.alfoks.popularmovies.util.TheMovieDbApi;
import io.reactivex.Single;

import android.support.annotation.NonNull;

public class RetrofitMoviesRepository implements MoviesRepository {

    @NonNull
    private final TheMovieDbApi theMovieDbApi;

    public RetrofitMoviesRepository(@NonNull TheMovieDbApi theMovieDbApi) {
        this.theMovieDbApi = theMovieDbApi;
    }

    @Override
    public Single<Movie> getMovie(long movieId) {
        return theMovieDbApi.getMovie(movieId);
    }

    @Override
    public Single<Movies> getMovies(SortBy sortBy, int page) {
        return theMovieDbApi.getMovies(sortBy, page);
    }
}
