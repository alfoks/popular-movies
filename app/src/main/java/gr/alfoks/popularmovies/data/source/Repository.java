package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface Repository {
    Single<Movie> getMovie(long movieId);
    Single<Movies> getMovies(SortBy sortBy, int page);
    Single<Boolean> updateFavorite(long movieId, boolean favorite);
    Observable<DataChange> getDataChangedObservable(DataChangeType... dataChangeType);
}
