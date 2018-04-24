package gr.alfoks.popularmovies.mvp.moviedetails;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;

public interface MovieDetailsContract {

    interface Presenter extends MvpPresenter<MovieDetailsContract.View> {
        void loadMovie(long movieId);
        void toggleFavorite();
        void seeAllReviewsClicked();
    }

    interface View extends MvpView {
        void onMovieLoaded(Movie movie);
        void onMovieUpdated(Movie movie);
        void playTrailer(String url);
        void showAllReviews(long movieId);
    }
}
