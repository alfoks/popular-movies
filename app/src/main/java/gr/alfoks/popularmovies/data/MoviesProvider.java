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

public class MoviesProvider extends ContentProvider {
    private static final int MOVIE = 100;
    private static final int MOVIES = 101;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_MOVIE + "/#", MOVIE);
        uriMatcher.addURI(MoviesTable.Content.CONTENT_AUTHORITY, MoviesTable.Content.PATH_MOVIE, MOVIES);
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
        switch(uriMatcher.match(uri)) {
            case MOVIE:
                return MoviesTable.Content.TYPE_DIR;
            case MOVIES:
                return MoviesTable.Content.TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch(uriMatcher.match(uri)) {
            case MOVIES:
                if(values == null || values.size() == 0) {
                    throw new IllegalArgumentException("Empty values");
                }
                long movieId = db.insertWithOnConflict(MoviesTable.NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                ContentUtils.notifyChange(getContext(), uri);
                return ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movieId);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(
        @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
        @Nullable String[] selectionArgs, @Nullable String sortOrder
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

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return qb.query(
            dbHelper.getWritableDatabase(),
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numDeleted;

        switch(uriMatcher.match(uri)) {
            case MOVIE:
                String movieId = ContentUtils.getStringId(uri);
                String newSelection = ContentUtils.appendFieldToSelection(selection, MoviesTable.Columns.ID);
                String[] newArgs = ContentUtils.appendValueToSelectionArgs(selectionArgs, movieId);

                numDeleted = db.delete(MoviesTable.NAME, newSelection, newArgs);
                break;
            case MOVIES:
                selection = ContentUtils.normalizeSelection(selection);
                numDeleted = db.delete(MoviesTable.NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(numDeleted != 0) {
            ContentUtils.notifyChange(getContext(), uri);
        }

        return numDeleted;
    }

    @Override
    public int update(
        @NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
        @Nullable String[] selectionArgs
    ) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        String newSelection;
        String[] newArgs;

        switch(uriMatcher.match(uri)) {
            case MOVIES:
                newSelection = selection;
                newArgs = selectionArgs;
                break;
            case MOVIE:
                String movieId = ContentUtils.getStringId(uri);
                newSelection = ContentUtils.appendFieldToSelection(selection, MoviesTable.Columns.ID);
                newArgs = ContentUtils.appendValueToSelectionArgs(selectionArgs, movieId);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        int numUpdated = db.update(MoviesTable.NAME, values, newSelection, newArgs);
        if(numUpdated != 0) {
            ContentUtils.notifyChange(getContext(), uri);
        }

        return numUpdated;
    }
}
