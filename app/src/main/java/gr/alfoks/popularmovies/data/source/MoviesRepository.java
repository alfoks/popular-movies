package gr.alfoks.popularmovies.data.source;

import java.util.Date;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.util.NetworkUtils;
import io.reactivex.Observable;
import io.reactivex.Single;

import android.content.Context;
import android.support.annotation.NonNull;

public class MoviesRepository implements Repository {
    private static final String TAG = MoviesRepository.class.getSimpleName();

    /** Two hours */
    private static final long CACHE_EXPIRATION_TIMEOUT = 2 * 60 * 60 * 1000;

    private final Context context;
    private final LocalMoviesDataSource localDataSource;
    private final MoviesDataSource remoteDataSource;

    public MoviesRepository(
        @NonNull Context context,
        @NonNull LocalMoviesDataSource localDataSource,
        @NonNull MoviesDataSource remoteDataSource
    ) {
        this.context = context;
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    @Override
    public Single<Movie> getMovie(long movieId) {
        final MoviesDataSource primary;
        final MoviesDataSource failover;

        if(shouldQueryLocalDataSource()) {
            primary = localDataSource;
            failover = remoteDataSource;
        } else {
            primary = remoteDataSource;
            failover = localDataSource;
        }

        return primary
            .getMovie(movieId)
            //If error occurs fetching from primary datasource, try failover
            .onErrorResumeNext(t -> failover.getMovie(movieId));
    }

    /**
     * Decide if we should query local or remote datasource for fetching a
     * movie. If cache refresh time has expired, we should query remote
     * repository. If however there is no internet connection, we should try
     * local datasource.
     */
    private boolean shouldQueryLocalDataSource() {
        if(!NetworkUtils.isNetworkAvailable(context)) return true;

        long cacheRefreshTime = Utils.getCacheRefreshTime(context);
        long cacheActiveTime = new Date().getTime() - cacheRefreshTime;

        return cacheActiveTime <= CACHE_EXPIRATION_TIMEOUT;
    }

    @Override
    public Single<Movies> getMovies(SortBy sortBy, int page) {
        if(shouldQueryLocalDataSource(sortBy, page)) {
            return getMoviesFromLocalDataSource(sortBy, page, true);
        } else {
            return getMoviesFromRemoteDataSource(sortBy, page, true);
        }
    }

    /**
     * Decide if we should query local or remote datasource for fetching movies.
     * If no results in local db <b>or</b> different size than TMDB page size
     * <b>or</b> cache refresh time has expired, we should query remote
     * repository. If however there is no internet connection, we should fetch
     * whatever there is in local datasource.
     */
    private boolean shouldQueryLocalDataSource(SortBy sortBy, int page) {
        if(!NetworkUtils.isNetworkAvailable(context)) return true;

        long cacheRefreshTime = Utils.getCacheRefreshTime(context);
        long cacheActiveTime = new Date().getTime() - cacheRefreshTime;
        if(cacheActiveTime > CACHE_EXPIRATION_TIMEOUT) return false;

        int numInLocal = localDataSource.getCount(sortBy, page);
        int pageSize = Utils.getPageSize(context);

        return numInLocal != 0 && numInLocal == pageSize;
    }

    /**
     * Query the local datasource for movies. If not available, query remote.
     *
     * @param failover when true, query remote datasource if error occurs or no
     *                 results.
     */
    @NonNull
    private Single<Movies> getMoviesFromLocalDataSource(SortBy sortBy, int page, boolean failover) {
        Single<Movies> moviesSingle = localDataSource.getMovies(sortBy, page);

        if(failover) {
            moviesSingle = moviesSingle
                .onErrorResumeNext(t -> getMoviesFromRemoteDataSource(sortBy, page, false))
                .flatMap(movies -> {
                    if(movies.getMovies().size() == 0) {
                        //If no results in local datasource, try remote
                        return getMoviesFromRemoteDataSource(sortBy, page, false);
                    }

                    return Single.fromObservable(Observable.just(movies));
                });
        }

        return moviesSingle;
    }

    /**
     * Query the remote datasource for movies. If not available, query local.
     *
     * @param failover when true, try local datasource if error occurs.
     */
    @NonNull
    private Single<Movies> getMoviesFromRemoteDataSource(SortBy sortBy, int page, boolean failover) {
        Single<Movies> moviesSingle = remoteDataSource.getMovies(sortBy, page);

        if(failover) {
            moviesSingle = moviesSingle
                .onErrorResumeNext(t -> getMoviesFromLocalDataSource(sortBy, page, false));
        }

        return moviesSingle
            .doOnSuccess(movies -> onRemoteSuccess(movies, sortBy, page));
    }

    /**
     * Save the results from remote datasource in local. Also update cache
     * settings.
     */
    private void onRemoteSuccess(Movies movies, SortBy sortBy, int page) {
        refreshCacheSettings(movies);
        saveMoviesInLocalDataSource(movies, sortBy, page);
    }

    private void refreshCacheSettings(Movies movies) {
        Utils.setCacheRefreshTime(context, new Date().getTime());
        Utils.setPageSize(context, movies.getPageSize());
    }

    private void saveMoviesInLocalDataSource(Movies movies, SortBy sortBy, int page) {
        localDataSource.saveMovies(movies, sortBy, page);
    }

    @Override
    public void reset() {
    }

    @Override
    public Single<Boolean> favorite(long movieId) {
        return null;
    }
}
