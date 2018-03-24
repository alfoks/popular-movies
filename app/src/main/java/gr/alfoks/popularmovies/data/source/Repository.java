package gr.alfoks.popularmovies.data.source;

import io.reactivex.Single;

public interface Repository extends MoviesDataSource {
    void reset();
    Single<Boolean> favorite(long movieId);
}
