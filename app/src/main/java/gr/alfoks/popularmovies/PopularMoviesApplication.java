package gr.alfoks.popularmovies;

import java.util.HashMap;

import gr.alfoks.popularmovies.mvp.model.TheMovieDbRepository;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsPresenter;
import gr.alfoks.popularmovies.mvp.movies.MoviesPresenter;
import gr.alfoks.popularmovies.util.RestClient;
import gr.alfoks.popularmovies.util.TheMovieDbApi;

import android.app.Application;
import android.support.annotation.NonNull;

public class PopularMoviesApplication extends Application {
    private MoviesPresenter moviesPresenter;
    private MovieDetailsPresenter movieDetailsPresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        TheMovieDbApi theMovieDbApi = createApi();
        TheMovieDbRepository repository = new TheMovieDbRepository(theMovieDbApi);
        moviesPresenter = new MoviesPresenter(repository);
        movieDetailsPresenter = new MovieDetailsPresenter(repository);
    }

    //Dependency injection functions below
    @NonNull
    private TheMovieDbApi createApi() {
        final HashMap<String, String> parameters = new HashMap<>();
        parameters.put("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        return new RestClient<>(TheMovieDbApi.BASE_URL, TheMovieDbApi.class, parameters, null).create();
    }

    @NonNull
    public MoviesPresenter provideMoviesPresenter() {
        return moviesPresenter;
    }

    @NonNull
    public MovieDetailsPresenter provideMovieDetailsPresenter() {
        return movieDetailsPresenter;
    }
}
