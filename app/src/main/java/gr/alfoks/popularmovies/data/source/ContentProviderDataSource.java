package gr.alfoks.popularmovies.data.source;

import java.util.ArrayList;
import java.util.Date;

import gr.alfoks.popularmovies.BuildConfig;
import gr.alfoks.popularmovies.data.ContentUtils;
import gr.alfoks.popularmovies.data.CursorIterable;
import gr.alfoks.popularmovies.data.MoviesProvider;
import gr.alfoks.popularmovies.data.table.MoviesSortTable;
import gr.alfoks.popularmovies.data.table.MoviesTable;
import gr.alfoks.popularmovies.data.table.ReviewsTable;
import gr.alfoks.popularmovies.data.table.TrailersTable;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.Review;
import gr.alfoks.popularmovies.mvp.model.Reviews;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.mvp.model.Trailer;
import gr.alfoks.popularmovies.mvp.model.Trailers;
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
    private static final int MOVIES_PAGE_SIZE = 20;
    private static final int REVIEWS_PAGE_SIZE = 10;

    @NonNull
    private final Context context;

    public ContentProviderDataSource(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public Single<Movie> loadMovie(long movieId) {
        Cursor c = getMovieCursor(movieId);

        return Observable
            .fromIterable(CursorIterable.from(c))
            .doAfterNext(cursor -> {
                if(cursor.getPosition() == cursor.getCount() - 1) {
                    cursor.close();
                }
            })
            .map(cursor -> Movie.builder().from(cursor).build())
            .elementAtOrError(0)
            .flatMap(this::loadMovieTrailers)
            .flatMap(this::loadMovieReviews);
    }

    private Single<Movie> loadMovieTrailers(Movie movie) {
        Uri uri = TrailersTable.Content.CONTENT_URI;
        String selection = TrailersTable.Columns.MOVIE_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(movie.id) };
        String sortOrder = TrailersTable.Columns.ID;

        Cursor c = getCursor(uri, selection, selectionArgs, sortOrder);

        return Observable
            .fromIterable(CursorIterable.from(c))
            .doAfterNext(cursor -> {
                if(cursor.getPosition() == cursor.getCount() - 1) {
                    cursor.close();
                }
            })
            .map(cursor -> Trailer.builder().from(c).build())
            .toList()
            .map(trailersList -> {
                Trailers trailers = new Trailers(trailersList);
                return Movie.builder().from(movie).setTrailers(trailers).build();
            });
    }

    private Single<Movie> loadMovieReviews(Movie movie) {
        return loadReviews(movie.id, 1)
            .map(reviews -> Movie.builder().from(movie).setReviews(reviews).build());
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
    public Single<Movies> loadMovies(SortBy sortBy, int page) {
        String sortOrder = MoviesSortTable.Columns.SORT_ORDER;
        String[] selectionArgs = getSortingSelectionArgs(sortBy);

        Uri uri = buildPagedUri(MoviesTable.Content.CONTENT_URI, page);
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
            .map(Movies::new);
    }

    @Override
    public Single<Reviews> loadReviews(long movieId, int page) {
        String sortOrder = ReviewsTable.Columns.SORT_ORDER;
        String selection = ReviewsTable.Columns.MOVIE_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(movieId) };

        Uri uri = buildPagedUri(ReviewsTable.Content.CONTENT_URI, page, REVIEWS_PAGE_SIZE);
        Cursor c = getCursor(uri, selection, selectionArgs, sortOrder);

        return Observable
            .fromIterable(CursorIterable.from(c))
            .doAfterNext(cursor -> {
                if(cursor.getPosition() == cursor.getCount() - 1) {
                    cursor.close();
                }
            })
            .map(cursor -> Review.builder().from(cursor).build())
            .toList()
            .map(Reviews::new);
    }

    private Uri buildPagedUri(Uri baseUri, int page) {
        return buildPagedUri(baseUri, page, MOVIES_PAGE_SIZE);
    }

    private Uri buildPagedUri(Uri baseUri, int page, int pageSize) {
        return baseUri
            .buildUpon()
            .appendQueryParameter(MoviesProvider.QUERY_PARAMETER_PAGE, String.valueOf(page))
            .appendQueryParameter(
                MoviesProvider.QUERY_PARAMETER_PAGE_SIZE,
                String.valueOf(pageSize)
            )
            .build();
    }

    @NonNull
    private String[] getSortingSelectionArgs(SortBy sortBy) {
        return new String[] { String.valueOf(sortBy.getId()) };
    }

    @Override
    public void saveMovie(Movie movie) {
        updateMovie(movie);
        updateTrailers(movie);
        saveReviews(movie.reviews, movie.id, 1);
    }

    private void updateMovie(Movie movie) {
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie.id);
        ContentValues values = movie.asValuesNoFavorite();

        context.getContentResolver().update(uri, values, null, null);
    }

    private void updateTrailers(Movie movie) {
        Uri uri = TrailersTable.Content.CONTENT_URI;
        String where = TrailersTable.Columns.MOVIE_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(movie.id) };

        context.getContentResolver().delete(uri, where, selectionArgs);
        context.getContentResolver().bulkInsert(uri, movie.trailers.asValues(movie.id));
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
    public void saveReviews(Reviews reviews, long movieId, int page) {
        Uri uri = ReviewsTable.Content.CONTENT_URI;
        context.getContentResolver().bulkInsert(uri, reviews.asValues(movieId, page));
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
