package gr.alfoks.popularmovies.data.source;

import java.util.NoSuchElementException;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.util.TheMovieDbApi;
import io.reactivex.Single;

import android.support.annotation.NonNull;

public class TheMovieDbDataSource implements MoviesDataSource {
    private static final String TAG = TheMovieDbDataSource.class.getSimpleName();
    @NonNull
    private final TheMovieDbApi theMovieDbApi;

    public TheMovieDbDataSource(@NonNull TheMovieDbApi theMovieDbApi) {
        this.theMovieDbApi = theMovieDbApi;
    }

    @Override
    public Single<Movie> getMovie(long movieId) {
        return theMovieDbApi.getMovie(movieId);
    }

    @Override
    public Single<Movies> getMovies(SortBy sortBy, int page) {
        return theMovieDbApi
            .getMovies(sortBy, page)
            .doOnSuccess(movies -> {
                if(movies.getMovies().size() == 0)
                    throw new NoSuchElementException("No more movies.");
            });
    }
}
