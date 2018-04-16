package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;

interface LocalMoviesDataSource extends MoviesDataSource {
    void saveMovie(Movie movie);
    void saveMovies(Movies movies, SortBy sortBy, int page);
    Single<Boolean> updateFavorite(long movieId, boolean favorite);
}
