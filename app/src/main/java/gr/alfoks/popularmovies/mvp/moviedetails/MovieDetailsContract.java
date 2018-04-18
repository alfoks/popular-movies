package gr.alfoks.popularmovies.mvp.moviedetails;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;

public interface MovieDetailsContract {

    interface Presenter extends MvpPresenter<MovieDetailsContract.View> {
        void loadMovie(long movieId);
        void toggleFavorite();
        void onBindTrailerView(TrailerView view, int position);
        int getTrailersCount();
        void onTrailerClicked(int position);
    }

    interface View extends MvpView {
        void onMovieLoaded(Movie movie);
        void onMovieUpdated(Movie movie);
        void playTrailer(String url);
    }

    interface TrailerView extends MvpView {
        void setThumbnail(String thumbnailUrl);
    }
}
