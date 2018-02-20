package gr.alfoks.popularmovies.testutils;

import java.io.IOException;

import gr.alfoks.popularmovies.model.Movies;
import io.reactivex.observers.TestObserver;
import retrofit2.HttpException;

import android.support.annotation.NonNull;

public class MoviesResult {
    private Movies movies;
    private int httpStatusCode;
    private String message;

    public Movies getMovies() {
        return movies;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @NonNull
    public static TestObserver<Movies> getTestObserver(final MoviesResult moviesResult) {
        return new TestObserver<Movies>() {
            @Override
            public void onNext(Movies movies) {
                moviesResult.movies = movies;
            }

            @Override
            public void
            onError(Throwable t) {
                if(t instanceof HttpException) {
                    moviesResult.httpStatusCode = ((HttpException)t).code();
                    try {
                        moviesResult.message = ((HttpException)t).response().errorBody().string();
                    } catch(IOException ex) {
                    }
                } else {
                    moviesResult.message = t.getMessage();
                }
            }
        };
    }
}
