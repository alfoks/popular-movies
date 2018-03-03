package gr.alfoks.popularmovies.mvp.model;

import io.reactivex.Single;

interface MoviesRepository {
    Single<Movie> getMovie(long movieId);
    Single<Movies> getMovies(SortBy sortBy, int page);
}
