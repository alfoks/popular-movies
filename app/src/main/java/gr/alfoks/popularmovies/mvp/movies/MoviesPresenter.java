package gr.alfoks.popularmovies.mvp.movies;

import java.util.HashMap;

import gr.alfoks.popularmovies.BuildConfig;
import gr.alfoks.popularmovies.mvp.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.util.RestClient;
import gr.alfoks.popularmovies.util.TheMovieDbApi;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.support.annotation.NonNull;

public class MoviesPresenter extends BasePresenter<MoviesContract.View>
    implements MoviesContract.Presenter {

    private final @NonNull TheMovieDbApi theMovieDbApi;
    private int nextPage = 1;
    private int totalPages = 1;

    public MoviesPresenter() {
        theMovieDbApi = createApi();
    }

    @Override
    public void fetchNextMoviesPage() {
        //Don't try to load more pages than those the api can provide
        if(nextPage > totalPages) return;

        final Single<Movies> moviesObservable = theMovieDbApi.getMovies(TheMovieDbApi.SortBy.POPULAR, nextPage++);

        moviesObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(createMoviesObserver());
    }

    @Override
    public void resetList() {
        nextPage = 1;
        getView().onListReset();
    }

    @Override
    public void showMovieDetails(Movie movie) {
        getView().onShowMovieDetails(movie);
    }

    @NonNull
    private TheMovieDbApi createApi() {
        final HashMap<String, String> parameters = new HashMap<>();
        parameters.put("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        return new RestClient<>(TheMovieDbApi.BASE_URL, TheMovieDbApi.class, parameters, null).create();
    }

    @NonNull
    private SingleObserver<Movies> createMoviesObserver() {
        return new SingleObserver<Movies>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Movies movies) {
                totalPages = movies.totalPages;
                getView().onMoviesFetched(movies);
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }
}
