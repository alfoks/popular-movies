package gr.alfoks.popularmovies.mvp.main;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.SortBy;

public interface MainContract {

    interface Presenter extends MvpPresenter<MainContract.View> {
        void attachMoviesFragment(SortBy sortBy);
        void showMovieDetails(long movieId);
    }

    interface View extends MvpView {
        void onAttachMoviesFragment(SortBy sortBy);
        void onShowMovieDetails(long movieId);
    }

}
