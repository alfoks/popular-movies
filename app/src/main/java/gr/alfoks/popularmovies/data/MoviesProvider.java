package gr.alfoks.popularmovies.data;

import java.util.ArrayList;

import gr.alfoks.popularmovies.data.table.MoviesSortTable;
import gr.alfoks.popularmovies.data.table.MoviesTable;
import gr.alfoks.popularmovies.util.Utils;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;
import static gr.alfoks.popularmovies.data.ContentUtils.notifyChange;

public class MoviesProvider extends ContentProvider {
    public static final int SQLITE_ERROR = -1;

    private static final int MOVIE = 100;
    private static final int MOVIES = 101;
    private static final int MOVIE_COUNT = 102;
    private static final int MOVIES_SORT = 103;

    private static final UriMatcher uriMatcher;
    private static final String UNKNOWN_URI = "Unknown uri: %s";
    /** Which page to query. 1 index based. **/
    public static final String QUERY_PARAMETER_PAGE = "page";
    /** Page size to be used in combination with {@link #QUERY_PARAMETER_PAGE} **/
    public static final String QUERY_PARAMETER_PAGE_SIZE = "page_size";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_TOTAL, MOVIE_COUNT);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesSortTable.Content.PATH_MOVIES_SORT, MOVIES_SORT);
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
            case MOVIES_SORT:
                return insertMovieSort(uri, values);
            default:
                throw new UnsupportedOperationException(String.format(UNKNOWN_URI, uri));
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            if(results.length != operations.size()) {
                throw new OperationApplicationException("Transaction failed");
            }
            db.setTransactionSuccessful();

            return results;
        } finally {
            db.endTransaction();
        }
    }

    private Uri insertMovie(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        checkNonNullValues(values);

        long movieId = db.insertWithOnConflict(MoviesTable.NAME, null, values, CONFLICT_REPLACE);
        if(movieId != SQLITE_ERROR) notifyChange(getContext(), uri);
        return ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movieId);
    }

    private Uri insertMovieSort(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        checkNonNullValues(values);

        long rowId = db.insertWithOnConflict(MoviesSortTable.NAME, null, values, CONFLICT_REPLACE);
        if(rowId != SQLITE_ERROR) notifyChange(getContext(), uri);
        return ContentUtils.withAppendedId(MoviesSortTable.Content.CONTENT_URI, rowId);
    }

    private void checkNonNullValues(ContentValues values) {
        if(values == null || values.size() == 0) {
            throw new IllegalArgumentException("Empty values");
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch(uriMatcher.match(uri)) {
            case MOVIES:
                return bulkInsert(MoviesTable.NAME, uri, values);
            case MOVIES_SORT:
                return bulkInsert(MoviesSortTable.NAME, uri, values);
            default:
                throw new UnsupportedOperationException(String.format(UNKNOWN_URI, uri));
        }
    }

    private int bulkInsert(String tableName, @NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numInserted = 0;
        int conflictAlgorithm = tableName.equals(MoviesTable.NAME) ? CONFLICT_FAIL : CONFLICT_REPLACE;

        db.beginTransaction();
        try {
            for(ContentValues recordValues : values) {
                checkNonNullValues(recordValues);
                long id = SQLITE_ERROR;

                try {
                    id = db.insertWithOnConflict(
                        tableName, null, recordValues, conflictAlgorithm
                    );
                } catch(SQLiteConstraintException ex) {
                    if(tableName.equals(MoviesTable.NAME)) {
                        String selection = MoviesTable.Columns.ID + "=?";
                        String[] selectionArgs = new String[] { recordValues.getAsString(MoviesTable.Columns.ID) };
                        id = db.update(tableName, recordValues, selection, selectionArgs);
                    }
                }

                if(id != SQLITE_ERROR) {
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
        String limitString = "";
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MoviesTable.NAME_FOR_JOIN);

        switch(uriMatcher.match(uri)) {
            case MOVIE:
                String id = String.valueOf(ContentUtils.parseId(uri));
                qb.appendWhere(MoviesTable.Columns.ID + "=" + id);
                break;
            case MOVIES:
                limitString = buildLimitString(uri);
                break;
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
            sortOrder,
            limitString
        );
    }

    private String buildLimitString(Uri uri) {
        int page = Utils.parseInt(uri.getQueryParameter(QUERY_PARAMETER_PAGE));
        int pageSize = Utils.parseInt(uri.getQueryParameter(QUERY_PARAMETER_PAGE_SIZE));

        if(page == 0) {
            return "";
        } else {
            return ((page - 1) * pageSize) + "," + pageSize;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numDeleted;

        switch(uriMatcher.match(uri)) {
            case MOVIE:
                numDeleted = deleteMovie(uri, selection, selectionArgs);
                break;
            case MOVIES:
                numDeleted = deleteMovies(selection, selectionArgs);
                break;
            case MOVIES_SORT:
                numDeleted = deleteSortOrders(selection, selectionArgs);
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

    private int deleteSortOrders(String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        selection = ContentUtils.normalizeSelection(selection);
        return db.delete(MoviesSortTable.NAME, selection, selectionArgs);
    }

    @Override
    public int update(
        @NonNull Uri uri, ContentValues values,
        String selection, String[] selectionArgs
    ) {
        int numUpdated;

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
