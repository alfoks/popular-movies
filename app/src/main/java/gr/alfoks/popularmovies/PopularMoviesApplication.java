package gr.alfoks.popularmovies;

import gr.alfoks.popularmovies.mvp.movies.MoviesPresenter;

import android.app.Application;

public class PopularMoviesApplication extends Application {
    private MoviesPresenter moviesPresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        moviesPresenter = new MoviesPresenter();
    }

    //Dependency injection functions below
    public MoviesPresenter provideMoviesPresenter() {
        return moviesPresenter;
    }
}
