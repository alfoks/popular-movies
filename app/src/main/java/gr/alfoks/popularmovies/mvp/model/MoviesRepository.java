package gr.alfoks.popularmovies.mvp.model;

import io.reactivex.Single;

public interface MoviesRepository {
    Single<Movie> getMovie(long movieId);
    Single<Movies> getMovies(SortBy sortBy, int page);
}
