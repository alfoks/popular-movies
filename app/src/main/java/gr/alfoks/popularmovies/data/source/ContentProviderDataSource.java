package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.data.ContentUtils;
import gr.alfoks.popularmovies.data.CursorIterable;
import gr.alfoks.popularmovies.data.table.MoviesSortTable;
import gr.alfoks.popularmovies.data.table.MoviesTable;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Observable;
import io.reactivex.Single;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;


import static gr.alfoks.popularmovies.data.MoviesProvider.SQLITE_ERROR;

public class ContentProviderDataSource implements LocalMoviesDataSource {
    @NonNull
    private final Context context;

    public ContentProviderDataSource(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public Single<Movie> getMovie(long movieId) {
        Cursor c = getMovieCursor(movieId);

        return Observable
            .fromIterable(new CursorIterable(c))
            .doAfterNext(cursor -> {
                if(cursor.getPosition() == cursor.getCount() - 1) {
                    cursor.close();
                }
            }).map(Movie::create).elementAtOrError(0);
    }

    private Cursor getMovieCursor(long movieId) {
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movieId);
        return getCursor(uri, null, null, null);
    }

    private Cursor getCursor(Uri uri, String selection, String[] selectionArgs, String sortOrder) {
        return context
            .getContentResolver()
            .query(uri, null, selection, selectionArgs, sortOrder);
    }

    @Override
    public Single<Movies> getMovies(SortBy sortBy, int page) {
        String sortOrder = MoviesSortTable.Columns.SORT_ORDER;
        String selection = MoviesSortTable.Columns.PAGE + "=? " +
            "AND " + MoviesSortTable.Columns.SORT_TYPE + "=?";
        String[] selectionArgs = new String[] { String.valueOf(page), String.valueOf(sortBy.getId()) };

        Uri uri = MoviesTable.Content.CONTENT_URI;
        Cursor c = getCursor(uri, selection, selectionArgs, sortOrder);

        return Observable
            .fromIterable(new CursorIterable(c))
            .doAfterNext(cursor -> {
                if(cursor.getPosition() == cursor.getCount() - 1) {
                    cursor.close();
                }
            })
            .map(Movie::create)
            .toList()
            .map(movies -> new Movies(movies, Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Override
    public int getCount(SortBy sortBy, int page) {
        Uri uri = MoviesTable.Content.CONTENT_URI_TOTAL;

        int totalPages = 0;
        String selection = MoviesSortTable.Columns.PAGE + "=? " +
            "AND " + MoviesSortTable.Columns.SORT_TYPE + "=?";

        String[] selectionArgs = new String[] { String.valueOf(page), String.valueOf(sortBy.getId()) };

        try(
            Cursor c = getCursor(uri, selection, selectionArgs, null)
        ) {
            if(c != null && c.moveToNext()) {
                totalPages = c.getInt(c.getColumnIndex(MoviesTable.Columns.Agr.TOTAL));
            }
        }

        return totalPages;
    }

    @Override
    public void saveMovies(Movies movies, SortBy sortBy, int page) {
        // We could do the 2 different bulk inserts in a transaction, but
        // we don't really care. If the insertion to the second table fails,
        // querying local datasource for movies will yield no results
        // (due to tables joining), just like if we used a transaction
        // and it failed.

        Uri uri = MoviesTable.Content.CONTENT_URI;
        int numInserted = context.getContentResolver().bulkInsert(uri, movies.asValues());
        if(numInserted == SQLITE_ERROR) return;

        uri = MoviesSortTable.Content.CONTENT_URI;
        context.getContentResolver().bulkInsert(uri, movies.asSortOrderValues(sortBy, page));
    }

    @Override
    public Single<Boolean> favorite(long movieId) {
        return null;
    }
}
