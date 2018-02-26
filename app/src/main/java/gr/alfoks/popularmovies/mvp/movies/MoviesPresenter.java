package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.mvp.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.TheMovieDbRepository;
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
    private final TheMovieDbRepository repository;
    private int nextPage = 1;
    private int totalPages = 1;

    public MoviesPresenter(@NonNull TheMovieDbRepository repository) {
        this.repository = repository;
    }

    @Override
    public void fetchNextMoviesPage() {
        //Don't try to load more pages than those the api can provide
        if(nextPage > totalPages) return;

        final Single<Movies> moviesObservable = repository.getMovies(SortBy.POPULAR, nextPage++);

        moviesObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(createMoviesObserver());
    }

    @Override
    public void resetList() {
        nextPage = 1;
        getView().onListReset();
    }

    @Override
    public void showMovieDetails(Movie movie) {
        getView().onShowMovieDetails(movie);
    }

    @NonNull
    private SingleObserver<Movies> createMoviesObserver() {
        return new SingleObserver<Movies>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Movies movies) {
                totalPages = movies.totalPages;
                getView().onMoviesFetched(movies);
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }
}
