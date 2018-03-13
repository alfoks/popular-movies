package gr.alfoks.popularmovies.testutil;

import java.io.IOException;

import gr.alfoks.popularmovies.mvp.model.Movies;
import io.reactivex.observers.TestObserver;
import okhttp3.ResponseBody;
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
                        ResponseBody error = ((HttpException)t).response().errorBody();
                        if(error != null) {
                            moviesResult.message = error.string();
                        }
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    moviesResult.message = t.getMessage();
                }
            }
        };
    }
}
