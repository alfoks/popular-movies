package gr.alfoks.popularmovies.data.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;

import gr.alfoks.popularmovies.BuildConfig;
import gr.alfoks.popularmovies.data.ContentUtils;
import gr.alfoks.popularmovies.data.CursorIterable;
import gr.alfoks.popularmovies.data.MoviesProvider;
import gr.alfoks.popularmovies.data.table.MoviesSortTable;
import gr.alfoks.popularmovies.data.table.MoviesTable;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Observable;
import io.reactivex.Single;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;


import static gr.alfoks.popularmovies.data.MoviesProvider.SQLITE_ERROR;

public final class ContentProviderDataSource implements LocalMoviesDataSource {
    private static final String SORTING_SELECTION = MoviesSortTable.Columns.SORT_TYPE + "=?";
    private static final int PAGE_SIZE = 20;

    @NonNull
    private final Context context;

    public ContentProviderDataSource(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public Single<Movie> getMovie(long movieId) {
        Cursor c = getMovieCursor(movieId);

        return Observable
            .fromIterable(CursorIterable.from(c))
            .doAfterNext(cursor -> {
                if(cursor.getPosition() == cursor.getCount() - 1) {
                    cursor.close();
                }
            })
            .map(cursor -> Movie.builder().from(cursor).build())
            .elementAtOrError(0);
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
        String[] selectionArgs = getSortingSelectionArgs(sortBy);

        Uri uri = buildMoviesUri(page);
        Cursor c = getCursor(uri, SORTING_SELECTION, selectionArgs, sortOrder);

        return Observable
            .fromIterable(CursorIterable.from(c))
            .doAfterNext(cursor -> {
                if(cursor.getPosition() == cursor.getCount() - 1) {
                    cursor.close();
                }
            })
            .map(cursor -> Movie.builder().from(cursor).build())
            .toList()
            .map(Movies::new)
            .doOnSuccess(movies -> {
                if(movies.getMovies().size() == 0)
                    throw new NoSuchElementException("No more movies.");
            });
    }

    private Uri buildMoviesUri(int page) {
        return MoviesTable.Content.CONTENT_URI
            .buildUpon()
            .appendQueryParameter(MoviesProvider.QUERY_PARAMETER_PAGE, String.valueOf(page))
            .appendQueryParameter(
                MoviesProvider.QUERY_PARAMETER_PAGE_SIZE,
                String.valueOf(PAGE_SIZE)
            )
            .build();
    }

    @NonNull
    private String[] getSortingSelectionArgs(SortBy sortBy) {
        return new String[] { String.valueOf(sortBy.getId()) };
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
    public Single<Boolean> updateFavorite(long movieId, boolean favorite) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(getDeleteSortingOperation(movieId, SortBy.FAVORITES));

        if(favorite) {
            operations.add(getInsertSortingOperation(movieId, SortBy.FAVORITES));
        }

        operations.add(getUpdateFavoriteOperation(movieId, favorite));

        return Single.create(e -> e.onSuccess(applyOperations(operations)));
    }

    private boolean applyOperations(ArrayList<ContentProviderOperation> operations)
        throws RemoteException, OperationApplicationException {

        String authority = BuildConfig.CONTENT_AUTHORITY;
        context.getContentResolver().applyBatch(authority, operations);

        return true;
    }

    private ContentProviderOperation getUpdateFavoriteOperation(long movieId, boolean favorite) {
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movieId);
        ContentValues values = new ContentValues();
        values.put(MoviesTable.Columns.FAVORITE, favorite ? 1 : 0);

        return ContentProviderOperation.newUpdate(uri).withValues(values).build();
    }

    private ContentProviderOperation getInsertSortingOperation(long movieId, SortBy sortBy) {
        Uri uri = MoviesSortTable.Content.CONTENT_URI;
        ContentValues orderValues = new ContentValues();
        orderValues.put(MoviesSortTable.Columns.MOVIE_ID, movieId);
        orderValues.put(MoviesSortTable.Columns.SORT_ORDER, new Date().getTime());
        orderValues.put(MoviesSortTable.Columns.SORT_TYPE, sortBy.getId());

        return ContentProviderOperation.newInsert(uri).withValues(orderValues).build();
    }

    private ContentProviderOperation getDeleteSortingOperation(long movieId, SortBy sortBy) {
        Uri uri = MoviesSortTable.Content.CONTENT_URI;
        String selection = MoviesSortTable.Columns.MOVIE_ID +
            "=? AND " + MoviesSortTable.Columns.SORT_TYPE + "=?";
        String[] selectionArgs = new String[] { String.valueOf(movieId), String.valueOf(sortBy.getId()) };

        return ContentProviderOperation.newDelete(uri).withSelection(selection, selectionArgs).build();
    }
}
