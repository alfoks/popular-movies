package gr.alfoks.popularmovies.mvp.moviedetails;

import gr.alfoks.popularmovies.mvp.MvpPresenter;
import gr.alfoks.popularmovies.mvp.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;

public interface MovieDetailsContract {

    interface Presenter extends MvpPresenter<MovieDetailsContract.View> {
    }

    interface View extends MvpView {
    }

}
