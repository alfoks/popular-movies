package gr.alfoks.popularmovies.mvp.model;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import gr.alfoks.popularmovies.data.table.MoviesSortTable;

import android.content.ContentValues;

public class Movies {
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
}
