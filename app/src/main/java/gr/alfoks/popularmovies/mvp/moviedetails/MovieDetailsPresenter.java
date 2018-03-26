package gr.alfoks.popularmovies.mvp.moviedetails;

import java.util.ArrayList;

import gr.alfoks.popularmovies.data.source.Repository;
import gr.alfoks.popularmovies.mvp.base.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Trailer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.support.annotation.NonNull;

public class MovieDetailsPresenter
    extends BasePresenter<MovieDetailsContract.View>
    implements MovieDetailsContract.Presenter {
    private final @NonNull
    Repository repository;

    public MovieDetailsPresenter(@NonNull Repository repository) {
        this.repository = repository;
    }

    @Override
    public void loadMovie(long movieId) {
        Single<Movie> movieObservable = repository.getMovie(movieId);

        movieObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(createMovieObserver());
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        //Todo: Show error
    }

    @NonNull
    private SingleObserver<Movie> createMovieObserver() {
        return new SingleObserver<Movie>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Movie movie) {
                if(isViewAttached()) {
                    getView().onMovieLoaded(movie);
                }
            }

            @Override
            public void onError(Throwable e) {
                MovieDetailsPresenter.this.onError(e);
            }
        };
    }

    @Override
    public void updateFavorite(long movieId, boolean favorite) {
        repository
            .updateFavorite(movieId, favorite)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(success -> getView().onFavoriteUpdated(favorite));
    }

    @Override
    public void loadTrailers(long movieId) {
        getView().onTrailersLoaded(new ArrayList<Trailer>());
    }
}
