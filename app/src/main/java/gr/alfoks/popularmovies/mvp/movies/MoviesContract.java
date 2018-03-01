package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;

public interface MoviesContract {

    interface Presenter extends MvpPresenter<MoviesContract.View> {
        void fetchNextMoviesPage(SortBy sortBy);
        void resetList();
        void showMovieDetails(Movie movie);
    }

    interface View extends MvpView {
        void onMoviesFetched(Movies movies);
        void onListReset();
        void onShowMovieDetails(Movie movie);
    }

}
