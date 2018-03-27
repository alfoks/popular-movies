package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.data.source.Repository;
import gr.alfoks.popularmovies.mvp.base.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.support.annotation.NonNull;

public class MoviesPresenter extends BasePresenter<MoviesContract.View>
    implements MoviesContract.Presenter {

    @NonNull
    private final Repository repository;
    private int nextPage = 1;
    private int totalPages = 1;
    private SortBy sortBy = SortBy.POPULAR;

    public MoviesPresenter(@NonNull Repository repository) {
        this.repository = repository;
        subscribeToDataChanges();
    }

    private void subscribeToDataChanges() {
        repository
            .dataChanged()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(n -> resetList(SortBy.FAVORITES));
    }

    @Override
    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        getView().onSetSortBy();
    }

    @Override
    public void fetchNextMoviesPage() {
        //Don't try to load more pages than those the api can provide
        if(nextPage > totalPages) return;

        final Single<Movies> moviesSingle = repository.getMovies(sortBy, nextPage);

        moviesSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(createMoviesObserver());
    }

    @Override
    public void resetList() {
        nextPage = 1;
        getView().onListReset();
    }

    /**
     * Reset list only if current sorting matches sortBy parameter
     */
    public void resetList(SortBy sortBy) {
        if(this.sortBy == sortBy) {
            resetList();
        }
    }

    @Override
    public void showMovieDetails(Movie movie) {
        getView().onShowMovieDetails(movie);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        //Todo: Show error
    }

    @NonNull
    private SingleObserver<Movies> createMoviesObserver() {
        return new SingleObserver<Movies>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Movies movies) {
                if(isViewAttached()) {
                    totalPages = movies.totalPages;
                    nextPage++;
                    getView().onMoviesFetched(movies);
                }
            }

            @Override
            public void onError(Throwable e) {
                MoviesPresenter.this.onError(e);
            }
        };
    }
}
