package gr.alfoks.popularmovies.data.source;

import java.util.Arrays;
import java.util.NoSuchElementException;

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

public final class MoviesRepository implements Repository {
    private final Context context;
    private final LocalMoviesDataSource localDataSource;
    private final MoviesDataSource remoteDataSource;

    private final PublishSubject<DataChange> notifier = PublishSubject.create();

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
    public Single<Movie> loadMovie(long movieId) {
        if(shouldQueryLocalDataSource()) {
            return loadMovieFromLocalDataSource(movieId);
        } else {
            return loadMovieFromRemoteDataSource(movieId, true);
        }
    }

    /**
     * Decide if we should query local or remote datasource for loading a movie.
     * Query local if there is no internet connection, remote otherwise.
     */
    private boolean shouldQueryLocalDataSource() {
        return !networkAvailabilityChecker.isNetworkAvailable(context);
    }

    /**
     * Load movie from remote datasource. On error try local.
     */
    private Single<Movie> loadMovieFromLocalDataSource(long movieId) {
        return localDataSource
            .loadMovie(movieId)
            .onErrorResumeNext(loadMovieFromRemoteDataSource(movieId, false));
    }

    private Single<Movie> loadMovieFromRemoteDataSource(long movieId, boolean failover) {
        Single<Movie> movieSingle = remoteDataSource.loadMovie(movieId);
        if(failover) {
            movieSingle = movieSingle.onErrorResumeNext(t -> localDataSource.loadMovie(movieId));
        }

        //When querying remote datasource check to see if movie is stored
        //locally and get its "favorite" field value
        movieSingle = movieSingle
            .flatMap(this::getMovieWithFavorite)
            .doOnSuccess(this::onRemoteSuccess);

        return movieSingle;
    }

    /**
     * Save movie details from remote datasource in local
     **/
    private void onRemoteSuccess(Movie movie) {
        localDataSource.saveMovie(movie);
    }

    @SuppressLint("CheckResult")
    private Single<Movie> getMovieWithFavorite(Movie movie) {
        final Single<Movie> movieSingle = localDataSource.loadMovie(movie.id);
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
    public Single<Movies> loadMovies(SortBy sortBy, int page) {
        final Single<Movies> moviesSingle;
        if(shouldQueryLocalDataSource(sortBy)) {
            moviesSingle = loadMoviesFromLocalDataSource(sortBy, page);
        } else {
            moviesSingle = loadMoviesFromRemoteDataSource(sortBy, page);
        }

        return moviesSingle.doOnSuccess(movies -> {
            if(movies.getMovies().size() == 0)
                throw new NoSuchElementException("No more movies.");
        });
    }

    /**
     * Decide if we should query local or remote datasource for loading movies.
     * Query local if sort by is "Favorites" <b>or</b> there is no internet
     * connection, remote otherwise.
     */
    private boolean shouldQueryLocalDataSource(SortBy sortBy) {
        final boolean isFavorites = sortBy == SortBy.FAVORITES;
        return isFavorites || shouldQueryLocalDataSource();
    }

    @NonNull
    private Single<Movies> loadMoviesFromLocalDataSource(SortBy sortBy, int page) {
        return localDataSource.loadMovies(sortBy, page);
    }

    @NonNull
    private Single<Movies> loadMoviesFromRemoteDataSource(SortBy sortBy, int page) {
        return remoteDataSource
            .loadMovies(sortBy, page)
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
