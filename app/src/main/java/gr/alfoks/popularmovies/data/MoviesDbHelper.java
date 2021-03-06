package gr.alfoks.popularmovies.data;

import gr.alfoks.popularmovies.data.table.MoviesSortTable;
import gr.alfoks.popularmovies.data.table.MoviesTable;
import gr.alfoks.popularmovies.data.table.ReviewsTable;
import gr.alfoks.popularmovies.data.table.TrailersTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class MoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MoviesTable.Ddl.CREATE_TABLE);
        db.execSQL(MoviesSortTable.Ddl.CREATE_TABLE);
        db.execSQL(TrailersTable.Ddl.CREATE_TABLE);
        db.execSQL(ReviewsTable.Ddl.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
