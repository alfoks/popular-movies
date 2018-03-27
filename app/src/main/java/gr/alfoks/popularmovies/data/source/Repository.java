package gr.alfoks.popularmovies.data.source;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public interface Repository extends MoviesDataSource {
    void reset();
    Single<Boolean> updateFavorite(long movieId, boolean favorite);
    PublishSubject<Boolean> dataChanged();
}
