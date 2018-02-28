package gr.alfoks.popularmovies.mvp.moviedetails;

import java.util.List;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Trailer;

public interface MovieDetailsContract {

    interface Presenter extends MvpPresenter<MovieDetailsContract.View> {
        void loadMovie(long movieId);
        void markFavorite(long movieId);
        void loadTrailers(long movieId);
    }

    interface View extends MvpView {
        void onMovieLoaded(Movie movie);
        void onFavored();
        void onTrailersLoaded(List<Trailer> trailers);
    }
}
