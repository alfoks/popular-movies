package gr.alfoks.popularmovies.mvp.main;

import gr.alfoks.popularmovies.mvp.base.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.SortBy;

public class MainPresenter extends BasePresenter<MainContract.View>
    implements MainContract.Presenter {
    @Override
    public void attachMoviesFragment(SortBy sortBy) {
        getView().onAttachMoviesFragment(sortBy);
    }

    @Override
    public void showMovieDetails(long movieId) {
        getView().onShowMovieDetails(movieId);
    }
}
