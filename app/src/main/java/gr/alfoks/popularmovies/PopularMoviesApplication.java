package gr.alfoks.popularmovies;

import gr.alfoks.popularmovies.mvp.BasePresenter;
import gr.alfoks.popularmovies.mvp.MvpView;
import gr.alfoks.popularmovies.mvp.movies.MoviesPresenter;

import android.app.Application;

public class PopularMoviesApplication extends Application {
    private MoviesPresenter moviesPresenter;
    private BasePresenter<MvpView> movieDetailsPresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        moviesPresenter = new MoviesPresenter();
        movieDetailsPresenter = new BasePresenter<>();
    }

    //Dependency injection functions below
    public MoviesPresenter provideMoviesPresenter() {
        return moviesPresenter;
    }

    public BasePresenter<MvpView> provideMovieDetailsPresenter() {
        return movieDetailsPresenter;
    }
}
