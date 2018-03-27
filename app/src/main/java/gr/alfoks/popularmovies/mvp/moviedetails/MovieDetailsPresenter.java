package gr.alfoks.popularmovies.mvp.moviedetails;

import java.util.ArrayList;

import gr.alfoks.popularmovies.data.source.Repository;
import gr.alfoks.popularmovies.mvp.base.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Movie;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
            .subscribe(this::onMovieLoaded, this::onError);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        //Todo: Show error
    }

    private void onMovieLoaded(Movie movie) {
        if(isViewAttached()) {
            getView().onMovieLoaded(movie);
        }
    }

    @Override
    public void updateFavorite(long movieId, boolean favorite) {
        repository
            .updateFavorite(movieId, favorite)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(success -> getView().onFavoriteUpdated(favorite), this::onError);
    }

    @Override
    public void loadTrailers(long movieId) {
        getView().onTrailersLoaded(new ArrayList<>());
    }
}
