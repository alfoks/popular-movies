package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;

public interface MoviesDataSource {
    Single<Movie> getMovie(long movieId);
    Single<Movies> getMovies(SortBy sortBy, int page);
}
