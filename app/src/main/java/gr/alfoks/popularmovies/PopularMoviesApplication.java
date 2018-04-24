package gr.alfoks.popularmovies;

import java.util.HashMap;

import gr.alfoks.popularmovies.api.RestClient;
import gr.alfoks.popularmovies.api.TheMovieDbApi;
import gr.alfoks.popularmovies.data.source.ContentProviderDataSource;
import gr.alfoks.popularmovies.data.source.MoviesRepository;
import gr.alfoks.popularmovies.data.source.Repository;
import gr.alfoks.popularmovies.data.source.TheMovieDbDataSource;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsContract;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsPresenter;
import gr.alfoks.popularmovies.mvp.movies.MoviesContract;
import gr.alfoks.popularmovies.mvp.movies.MoviesPresenter;
import gr.alfoks.popularmovies.mvp.reviews.ReviewsContract;

import android.app.Application;
import android.support.annotation.NonNull;

public class PopularMoviesApplication extends Application {
    private MoviesRepository repository;
    private MoviesContract.Presenter moviesPresenter;
    private MovieDetailsContract.Presenter movieDetailsPresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        TheMovieDbApi theMovieDbApi = createApi();
        ContentProviderDataSource contentProviderDataSource = new ContentProviderDataSource(this);
        TheMovieDbDataSource theMovieDbDataSource = new TheMovieDbDataSource(theMovieDbApi);
        repository = new MoviesRepository(this, contentProviderDataSource, theMovieDbDataSource);

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
    public MoviesContract.Presenter provideMoviesPresenter() {
        return moviesPresenter;
    }

    @NonNull
    public MovieDetailsContract.Presenter provideMovieDetailsPresenter() {
        return movieDetailsPresenter;
    }

    @NonNull
    public Repository provideRepository() {
        return repository;
    }
}
