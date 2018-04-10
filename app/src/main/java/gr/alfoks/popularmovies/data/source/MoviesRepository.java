package gr.alfoks.popularmovies.data.source;

import java.util.Arrays;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.util.NetworkAvailabilityChecker;
import gr.alfoks.popularmovies.util.NetworkUtils;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

public class MoviesRepository implements Repository {
    private final Context context;
    private final LocalMoviesDataSource localDataSource;
    private final MoviesDataSource remoteDataSource;

    private PublishSubject<DataChange> notifier = PublishSubject.create();

    private NetworkAvailabilityChecker networkAvailabilityChecker = NetworkUtils.getInstance();

    public MoviesRepository(
        @NonNull Context context,
        @NonNull LocalMoviesDataSource localDataSource,
        @NonNull MoviesDataSource remoteDataSource
    ) {
        this.context = context;
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public void setNetworkAvailabilityChecker(NetworkAvailabilityChecker checker) {
        networkAvailabilityChecker = checker;
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
     * movie. Query local if there is no internet connection, remote otherwise.
     */
    private boolean shouldQueryLocalDataSource() {
        return !networkAvailabilityChecker.isNetworkAvailable(context);
    }

    /**
     * Fetch movie from remote datasource. On error try local.
     */
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

    @SuppressLint("CheckResult")
    private Single<Movie> getMovieWithFavorite(Movie movie) {
        final Single<Movie> movieSingle = localDataSource.getMovie(movie.id);
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
        if(shouldQueryLocalDataSource(sortBy)) {
            return getMoviesFromLocalDataSource(sortBy, page);
        } else {
            return getMoviesFromRemoteDataSource(sortBy, page);
        }
    }

    /**
     * Decide if we should query local or remote datasource for fetching movies.
     * Query local if sort by is "Favorites" <b>or</b> there is no internet
     * connection, remote otherwise.
     */
    private boolean shouldQueryLocalDataSource(SortBy sortBy) {
        final boolean isFavorites = sortBy == SortBy.FAVORITES;
        return isFavorites || shouldQueryLocalDataSource();
    }

    @NonNull
    private Single<Movies> getMoviesFromLocalDataSource(SortBy sortBy, int page) {
        return localDataSource.getMovies(sortBy, page);
    }

    @NonNull
    private Single<Movies> getMoviesFromRemoteDataSource(SortBy sortBy, int page) {
        return remoteDataSource
            .getMovies(sortBy, page)
            .doOnSuccess(movies -> onRemoteSuccess(movies, sortBy, page));
    }

    /**
     * Save the results from remote datasource in local.
     */
    private void onRemoteSuccess(Movies movies, SortBy sortBy, int page) {
        saveMoviesInLocalDataSource(movies, sortBy, page);
    }

    private void saveMoviesInLocalDataSource(Movies movies, SortBy sortBy, int page) {
        localDataSource.saveMovies(movies, sortBy, page);
    }

    @Override
    public Single<Boolean> updateFavorite(long movieId, boolean favorite) {
        final Movie movie = Movie.builder().setId(movieId).setFavorite(favorite).build();
        return localDataSource
            .updateFavorite(movieId, favorite)
            .doOnSuccess(success -> notifyDataChanged(new DataChange(DataChangeType.FAVORITE, movie)));
    }

    /**
     * Clients should subscribe to the observable subject returned by this
     * method, in order to be notified about changes in data.
     *
     * @param dataChangeType varargs of {@link DataChangeType} containing the
     *                       data change types the client wishes to subscribe
     *                       to.
     */
    @Override
    public Observable<DataChange> getDataChangedObservable(DataChangeType... dataChangeType) {
        return notifier.filter(data -> Arrays.asList(dataChangeType).contains(data.type));
    }

    private void notifyDataChanged(DataChange dataChange) {
        notifier.onNext(dataChange);
    }
}
