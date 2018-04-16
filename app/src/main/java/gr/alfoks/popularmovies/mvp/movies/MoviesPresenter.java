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
        //TODO: Account for fact that unfavorited movie maybe favorited again before returning to list
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
    public void loadMovies() {
        final Single<Movies> moviesSingle = repository.loadMovies(sortBy, nextPage);

        moviesSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onMoviesLoaded, this::onError);
    }

    private void onMoviesLoaded(Movies movies) {
        nextPage++;
        getView().onMoviesLoaded(movies);
    }

    @Override
    public void onError(Throwable e) {
        getView().onErrorLoadingMovies(e);
    }

    @Override
    public void movieClicked(Movie movie) {
        getView().onMovieClicked(movie);
    }

    @Override
    public void onConnectivityChanged(boolean connectionOn) {
        getView().reset();
        resetPage();
        loadMovies();
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
        public void onMoviesLoaded(Movies movies) {
        }

        @Override
        public void onErrorLoadingMovies(Throwable e) {
        }

        @Override
        public void onMovieClicked(Movie movie) {
        }
    };
}
