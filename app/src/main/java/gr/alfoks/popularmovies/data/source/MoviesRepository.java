package gr.alfoks.popularmovies.data.source;

import java.util.Date;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.util.NetworkUtils;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.subjects.PublishSubject;

import android.content.Context;
import android.support.annotation.NonNull;

public class MoviesRepository implements Repository {
    private static final String TAG = MoviesRepository.class.getSimpleName();

    /** Two hours */
    private static final long CACHE_EXPIRATION_TIMEOUT = 2 * 60 * 60 * 1000;

    private final Context context;
    private final LocalMoviesDataSource localDataSource;
    private final MoviesDataSource remoteDataSource;

    private PublishSubject<Boolean> notifier = PublishSubject.create();

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
        if(shouldQueryLocalDataSource()) {
            return getMovieFromLocalDataSource(movieId);
        } else {
            return getMovieFromRemoteDataSource(movieId, true);
        }
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

    private Single<Movie> getMovieFromLocalDataSource(long movieId) {
        return localDataSource
            .getMovie(movieId)
            .onErrorResumeNext(getMovieFromRemoteDataSource(movieId, false));
    }

    private Single<Movie> getMovieFromRemoteDataSource(long movieId, boolean failover) {
        Single<Movie> movieSingle = remoteDataSource.getMovie(movieId);
        if(failover) {
            movieSingle = movieSingle.onErrorResumeNext(t -> localDataSource.getMovie(movieId));
        }

        //When querying remote datasource check to see if movie is stored
        //locally and get its "favorite" field value
        movieSingle = movieSingle.flatMap(this::getMovieWithFavorite);

        return movieSingle;
    }

    private SingleSource<Movie> getMovieWithFavorite(Movie movie) {
        Single<Movie> movieSingle = localDataSource.getMovie(movie.id);
        final Movie[] movieWithFavorite = new Movie[1];

        movieSingle
            .onErrorReturnItem(movie)
            .subscribe(m -> movieWithFavorite[0] = Movie.builder().from(movie).setFavorite(m.favorite).build());

        if(movieWithFavorite[0] != null) {
            return Single.create(e -> e.onSuccess(movieWithFavorite[0]));
        } else {
            return Single.create(e -> e.onSuccess(movie));
        }
    }

    @Override
    public Single<Movies> getMovies(SortBy sortBy, int page) {
        if(shouldQueryLocalDataSource(sortBy, page)) {
            //Don't try remote on failover, if we are requesting favorites
            boolean failover = sortBy != SortBy.FAVORITES;
            return getMoviesFromLocalDataSource(sortBy, page, failover);
        } else {
            return getMoviesFromRemoteDataSource(sortBy, page, true);
        }
    }

    /**
     * Decide if we should query local or remote datasource for fetching movies.
     * If no results in local db <b>or</b> different size than TMDB page size
     * <b>or</b> cache refresh time has expired, we should query remote
     * repository. If however there is no internet connection, we should fetch
     * whatever there is in local datasource. Also query local if we are
     * requesting favorites.
     */
    private boolean shouldQueryLocalDataSource(SortBy sortBy, int page) {
        if(sortBy == SortBy.FAVORITES) return true;
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
                .onErrorResumeNext(t -> getMoviesFromRemoteDataSource(sortBy, page, false));
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
    public Single<Boolean> updateFavorite(long movieId, boolean favorite) {
        return localDataSource
            .updateFavorite(movieId, favorite)
            .doOnSuccess(success -> notifyDataChanged());
    }

    /**
     * Clients should subscribe to the observable subject returned by this
     * method, in order to be notified about changes in data.
     */
    @Override
    public PublishSubject<Boolean> dataChanged() {
        return notifier;
    }

    private void notifyDataChanged() {
        notifier.onNext(true);
    }
}
