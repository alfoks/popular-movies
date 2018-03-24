package gr.alfoks.popularmovies.mvp.model;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import gr.alfoks.popularmovies.data.table.MoviesSortTable;

import android.content.ContentValues;

public class Movies {
    @SerializedName("results")
    private final List<Movie> movies;
    @SerializedName("total_pages")
    public final int totalPages;
    @SerializedName("total_results")
    private final int totalResults;

    public Movies(List<Movie> movies, int totalPages, int totalResults) {
        this.movies = movies;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public List<Movie> getMovies() {
        return Collections.unmodifiableList(movies);
    }

    public ContentValues[] asValues() {
        ContentValues[] values = new ContentValues[movies.size()];
        for(int i = 0; i < movies.size(); i++) {
            values[i] = movies.get(i).asValues();
        }

        return values;
    }

    public ContentValues[] asSortOrderValues(SortBy sortBy, int page) {
        ContentValues[] values = new ContentValues[movies.size()];
        for(int i = 0; i < movies.size(); i++) {
            ContentValues orderValues = new ContentValues();
            orderValues.put(MoviesSortTable.Columns.MOVIE_ID, movies.get(i).id);
            orderValues.put(MoviesSortTable.Columns.PAGE, page);
            orderValues.put(MoviesSortTable.Columns.SORT_ORDER, i);
            orderValues.put(MoviesSortTable.Columns.SORT_TYPE, sortBy.getId());
            values[i] = orderValues;
        }

        return values;
    }

    public int getPageSize() {
        if(totalPages > 0) {
            return (int)Math.ceil((double)totalResults / totalPages);
        }
        return 0;
    }
}
