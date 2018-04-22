package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.Reviews;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;

interface MoviesDataSource {
    Single<Movie> loadMovie(long movieId);
    Single<Movies> loadMovies(SortBy sortBy, int page);
    Single<Reviews> loadReviews(long movieId, int page);
}
