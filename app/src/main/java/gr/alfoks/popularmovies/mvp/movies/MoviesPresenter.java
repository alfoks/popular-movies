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
    implements MoviesContract.Presenter, MoviesContract.ListPresenter {

    @NonNull
    private final Repository repository;
    private int nextPage = 1;
    private Movies movies = Movies.empty();
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
                if(sortBy == SortBy.FAVORITES) {
                    favoriteUpdated(dataChange);
                }
                break;
        }
    }

    private void favoriteUpdated(DataChange dataChange) {
        if(!(dataChange.data instanceof Movie)) return;
        Movie movie = (Movie)dataChange.data;
        if(movie.favorite) {
            movies = movies.addMovie(movie);
        } else {
            movies = movies.removeMovie(movie);
        }
        getView().onDataChanged();
    }

    @Override
    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        reset();
        getView().reset();
    }

    private void reset() {
        nextPage = 1;
        movies = Movies.empty();
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
        this.movies = this.movies.mergeWith(movies);
        nextPage++;
        getView().onDataChanged();
    }

    @Override
    public void onBindMovieView(MoviesContract.ListItemView view, int position) {
        Movie movie = movies.getMovies().get(position);
        view.bindData(movie);
    }

    @Override
    public int getMoviesCount() {
        return movies.getMovies().size();
    }

    @Override
    public void movieClicked(int position) {
        Movie movie = movies.getMovies().get(position);
        getView().showMovieDetails(movie);
    }

    @Override
    public void onError(Throwable e) {
        getView().onErrorLoadingMovies(e);
    }

    @Override
    public void onConnectivityChanged(boolean connectionOn) {
        reset();
        getView().reset();
        loadMovies();
    }

    @Override
    protected MoviesContract.View getView() {
        if(isViewAttached()) return super.getView();

        return nullView;
    }

    private final MoviesContract.View nullView = new MoviesContract.View() {
        @Override
        public void onDataChanged() {
        }

        @Override
        public void reset() {
        }

        @Override
        public void onErrorLoadingMovies(Throwable e) {
        }

        @Override
        public void showMovieDetails(Movie movie) {
        }
    };
}
