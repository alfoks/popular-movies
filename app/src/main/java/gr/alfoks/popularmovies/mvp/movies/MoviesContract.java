package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;

public interface MoviesContract {

    interface Presenter extends MvpPresenter<MoviesContract.View> {
        void setSortBy(SortBy sortBy);
        void loadMovies();
        void movieClicked(Movie movie);
    }

    interface View extends MvpView {
        void onMovieRemoved(Movie movie);
        void reset();
        void onMoviesLoaded(Movies movies);
        void onErrorLoadingMovies(Throwable e);
        void onMovieClicked(Movie movie);
    }

}
