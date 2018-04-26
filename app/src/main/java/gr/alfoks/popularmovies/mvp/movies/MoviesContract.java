package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.SortBy;

public interface MoviesContract {

    interface Presenter extends MvpPresenter<MoviesContract.View> {
        void setSortBy(SortBy sortBy);
        void loadMovies();
    }

    interface View extends MvpView {
        void onDataChanged();
        void reset();
        void onErrorLoadingMovies(Throwable e);
        void showMovieDetails(Movie movie);
    }

    interface ListPresenter {
        void onBindMovieView(MoviesContract.ListItemView view, int position);
        int getMoviesCount();
        void movieClicked(int position);
    }

    interface ListItemView extends MvpView {
        void bindData(Movie movie);
    }

}
