package gr.alfoks.popularmovies.mvp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import gr.alfoks.popularmovies.data.table.MoviesSortTable;
import io.reactivex.Observable;

import android.annotation.SuppressLint;
import android.content.ContentValues;

public final class Movies {
    @SerializedName("results")
    private final List<Movie> movies;

    public Movies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return Collections.unmodifiableList(movies);
    }

    public ContentValues[] asValues() {
        ContentValues[] values = new ContentValues[movies.size()];
        for(int i = 0; i < movies.size(); i++) {
            values[i] = movies.get(i).asValuesNoFavorite();
        }

        return values;
    }

    public ContentValues[] asSortOrderValues(SortBy sortBy, int page) {
        long order = page * 10000000;
        ContentValues[] values = new ContentValues[movies.size()];
        for(int i = 0; i < movies.size(); i++) {
            ContentValues orderValues = new ContentValues();
            orderValues.put(MoviesSortTable.Columns.MOVIE_ID, movies.get(i).id);
            orderValues.put(MoviesSortTable.Columns.SORT_ORDER, order + i);
            orderValues.put(MoviesSortTable.Columns.SORT_TYPE, sortBy.getId());
            values[i] = orderValues;
        }

        return values;
    }

    public Movies mergeWith(Movies movies) {
        List<Movie> movieList = new ArrayList<>(this.movies);
        movieList.addAll(movies.getMovies());
        return new Movies(movieList);
    }

    public Movies addMovie(Movie movie) {
        List<Movie> movieList = new ArrayList<>(this.movies);
        movieList.add(movie);
        return new Movies(movieList);
    }

    @SuppressLint("CheckResult")
    public Movies removeMovie(Movie movie) {
        List<Movie> movieList = new ArrayList<>(this.movies);
        final Movies[] newMovies = new Movies[1];

        Observable
            .fromIterable(movieList)
            .filter(m -> m.id == movie.id)
            .subscribe(m -> {
                movieList.remove(m);
                newMovies[0] = new Movies(movieList);
            }, throwable -> {
            });

        return newMovies[0];
    }

    public static Movies empty() {
        return new Movies(new ArrayList<>());
    }
}
