package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;

public interface LocalMoviesDataSource extends MoviesDataSource {
    int getCount(SortBy sortBy, int page);
    void saveMovies(Movies movies, SortBy sortBy, int page);
    Single<Boolean> favorite(long movieId);
}
