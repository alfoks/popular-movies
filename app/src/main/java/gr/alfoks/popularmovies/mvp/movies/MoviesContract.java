package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.mvp.MvpPresenter;
import gr.alfoks.popularmovies.mvp.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;

public interface MoviesContract {

    interface Presenter extends MvpPresenter<MoviesContract.View> {
        void fetchNextMoviesPage();
        void resetList();
        void showMovieDetails(Movie movie);
    }

    interface View extends MvpView {
        void onMoviesFetched(Movies movies);
        void onListReset();
        void onShowMovieDetails(Movie movie);
    }

}
