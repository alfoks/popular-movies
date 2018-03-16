package gr.alfoks.popularmovies.data;

import gr.alfoks.popularmovies.data.table.MoviesTable;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;
import static gr.alfoks.popularmovies.data.ContentUtils.notifyChange;

public class MoviesProvider extends ContentProvider {
    private static final int SQLITE_ERROR = -1;

    private static final int MOVIE = 100;
    private static final int MOVIES = 101;
    private static final int MOVIE_COUNT = 102;
    private static final UriMatcher uriMatcher;
    private static final String UNKNOWN_URI = "Unknown uri: %s";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_TOTAL, MOVIE_COUNT);
    }

    private SQLiteOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch(uriMatcher.match(uri)) {
            case MOVIES:
                return insertMovie(uri, values);
            default:
                throw new UnsupportedOperationException(String.format(UNKNOWN_URI, uri));
        }
    }

    private Uri insertMovie(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(values == null || values.size() == 0) {
            throw new IllegalArgumentException("Empty values");
        }

        long movieId = db.insertWithOnConflict(MoviesTable.NAME, null, values, CONFLICT_REPLACE);
        if(movieId != SQLITE_ERROR) notifyChange(getContext(), uri);
        return ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movieId);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch(uriMatcher.match(uri)) {
            case MOVIES:
                return bulkInsertMovies(uri, values);
            default:
                throw new UnsupportedOperationException(String.format(UNKNOWN_URI, uri));
        }
    }

    private int bulkInsertMovies(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numInserted = 0;

        db.beginTransaction();
        try {
            for(ContentValues oneMovieValues : values) {
                long id = db.insertWithOnConflict(
                    MoviesTable.NAME, null, oneMovieValues, CONFLICT_REPLACE
                );

                if(id != -1) {
                    numInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if(numInserted > 0) {
            notifyChange(getContext(), uri);
        }

        return numInserted;
    }

    @Nullable
    @Override
    public Cursor query(
        @NonNull Uri uri, String[] projection, String selection,
        String[] selectionArgs, String sortOrder
    ) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MoviesTable.NAME);

        switch(uriMatcher.match(uri)) {
            case MOVIE: {
                String id = String.valueOf(ContentUtils.parseId(uri));
                qb.appendWhere(MoviesTable.Columns.ID + "=" + id);
                break;
            }

            case MOVIES: {
                break;
            }

            case MOVIE_COUNT:
                projection = new String[] { "count(1) AS total" };
                break;
            default:
                throw new UnsupportedOperationException(String.format(UNKNOWN_URI, uri));
        }

        return qb.query(
            dbHelper.getReadableDatabase(),
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numDeleted = 0;

        switch(uriMatcher.match(uri)) {
            case MOVIE:
                numDeleted = deleteMovie(uri, selection, selectionArgs);
                break;
            case MOVIES:
                numDeleted = deleteMovies(selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(String.format(UNKNOWN_URI, uri));
        }

        if(numDeleted != 0) {
            notifyChange(getContext(), uri);
        }

        return numDeleted;
    }

    private int deleteMovie(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        String movieId = ContentUtils.getStringId(uri);
        String newSelection = ContentUtils.appendFieldToSelection(selection, MoviesTable.Columns.ID);
        String[] newArgs = ContentUtils.appendValueToSelectionArgs(selectionArgs, movieId);

        return db.delete(MoviesTable.NAME, newSelection, newArgs);
    }

    private int deleteMovies(String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        selection = ContentUtils.normalizeSelection(selection);
        return db.delete(MoviesTable.NAME, selection, selectionArgs);
    }

    @Override
    public int update(
        @NonNull Uri uri, ContentValues values,
        String selection, String[] selectionArgs
    ) {
        int numUpdated = 0;

        switch(uriMatcher.match(uri)) {
            case MOVIE:
                numUpdated = updateMovie(uri, values, selection, selectionArgs);
                break;
            case MOVIES:
                numUpdated = updateMovies(values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(String.format(UNKNOWN_URI, uri));
        }

        if(numUpdated != 0) {
            notifyChange(getContext(), uri);
        }

        return numUpdated;
    }

    private int updateMovie(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final String movieId = ContentUtils.getStringId(uri);
        selection = ContentUtils.appendFieldToSelection(selection, MoviesTable.Columns.ID);
        selectionArgs = ContentUtils.appendValueToSelectionArgs(selectionArgs, movieId);

        return db.update(MoviesTable.NAME, values, selection, selectionArgs);
    }

    private int updateMovies(ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(MoviesTable.NAME, values, selection, selectionArgs);
    }
}
