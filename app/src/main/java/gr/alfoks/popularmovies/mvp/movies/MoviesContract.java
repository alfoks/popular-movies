package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.MvpPresenter;
import gr.alfoks.popularmovies.mvp.MvpView;

public interface MoviesContract {

    interface Presenter extends MvpPresenter<MoviesContract.View> {
        void fetchNextMoviesPage();
        void resetList();
    }

    interface View extends MvpView {
        void onMoviesFetched(Movies movies);
        void onListReset();
    }

}
