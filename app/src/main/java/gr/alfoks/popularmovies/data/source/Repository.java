package gr.alfoks.popularmovies.data.source;

import io.reactivex.Single;

public interface Repository extends MoviesDataSource {
    void reset();
    Single<Boolean> updateFavorite(long movieId, boolean favorite);
}
