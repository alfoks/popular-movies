package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.data.source.DataChange;
import gr.alfoks.popularmovies.data.source.DataChangeType;
import gr.alfoks.popularmovies.data.source.Repository;
import gr.alfoks.popularmovies.mvp.base.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

public final class MoviesPresenter extends BasePresenter<MoviesContract.View>
    implements MoviesContract.Presenter {

    @NonNull
    private final Repository repository;
    private int nextPage = 1;
    private SortBy sortBy = SortBy.POPULAR;

    public MoviesPresenter(@NonNull Repository repository) {
        this.repository = repository;
        subscribeToDataChanges();
    }

    @SuppressLint("CheckResult")
    private void subscribeToDataChanges() {
        repository
            .getDataChangedObservable(DataChangeType.FAVORITE)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::dataChanged);
    }

    private void dataChanged(DataChange dataChange) {
        switch(dataChange.type) {
            case FAVORITE:
                removeFavorite(dataChange);
                break;
        }
    }

    private void removeFavorite(DataChange dataChange) {
        //TODO: Implement it correctly (i.e. move data access in view adapter, to presenter
        if(!(dataChange.data instanceof Movie)) return;
        Movie movie = (Movie)dataChange.data;
        if(!movie.favorite) {
            getView().onMovieRemoved(movie);
        }
    }

    @Override
    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        resetPage();
        getView().reset();
    }

    private void resetPage() {
        nextPage = 1;
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchNextMoviesPage() {
        final Single<Movies> moviesSingle = repository.getMovies(sortBy, nextPage);

        moviesSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onMoviesFetched, this::onError);
    }

    private void onMoviesFetched(Movies movies) {
        nextPage++;
        getView().onMoviesFetched(movies);
    }

    @Override
    public void onError(Throwable e) {
        getView().onErrorFetchingMovies(e);
    }

    @Override
    public void showMovieDetails(Movie movie) {
        getView().onShowMovieDetails(movie);
    }

    @Override
    public void onConnectivityChanged(boolean connectionOn) {
        getView().reset();
        resetPage();
        fetchNextMoviesPage();
    }

    @Override
    protected MoviesContract.View getView() {
        if(isViewAttached()) return super.getView();

        return nullView;
    }

    private final MoviesContract.View nullView = new MoviesContract.View() {
        @Override
        public void onMovieRemoved(Movie movie) {
        }

        @Override
        public void reset() {
        }

        @Override
        public void onMoviesFetched(Movies movies) {
        }

        @Override
        public void onErrorFetchingMovies(Throwable e) {
        }

        @Override
        public void onShowMovieDetails(Movie movie) {
        }
    };
}
