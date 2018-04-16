package gr.alfoks.popularmovies.data.table;

import android.net.Uri;


import static gr.alfoks.popularmovies.data.Constants.BASE_CONTENT_URI;

public final class MoviesTable {
    public static final String NAME = "Movies";
    public static final String NAME_FOR_JOIN = NAME + " m " +
        "INNER JOIN " +
        MoviesSortTable.NAME + " ms " +
        "ON " +
        "ms." + MoviesSortTable.Columns.MOVIE_ID +
        " = m." + Columns.ID;

    public static final class Columns {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String ORIGINAL_TITLE = "originalTitle";
        public static final String POSTER_PATH = "posterPath";
        public static final String OVERVIEW = "overview";
        public static final String VOTE_AVERAGE = "voteAverage";
        /** Stored as milliseconds since 01/01/1970 00:00:00 GMT */
        public static final String RELEASE_DATE = "releaseDate";
        public static final String RUNTIME = "runtime";
        public static final String FAVORITE = "favorite";
    }

    public static final class Content {
        public static final String PATH_MOVIES = "movie";
        public static final String PATH_MOVIE = PATH_MOVIES + "/#";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
            .buildUpon()
            .appendPath(PATH_MOVIES)
            .build();
    }

    public static final class Ddl {
        public static final String CREATE_TABLE = "CREATE TABLE " + NAME + " (" +
            Columns.ID + " INTEGER PRIMARY KEY," +
            Columns.TITLE + " TEXT," +
            Columns.ORIGINAL_TITLE + " TEXT," +
            Columns.POSTER_PATH + " TEXT," +
            Columns.OVERVIEW + " TEXT," +
            Columns.VOTE_AVERAGE + " REAL," +
            Columns.RELEASE_DATE + " DATETIME," +
            Columns.RUNTIME + " INTEGER," +
            Columns.FAVORITE + " INTEGER DEFAULT 0" +
            ");";
    }
}
