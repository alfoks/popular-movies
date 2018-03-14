package gr.alfoks.popularmovies.data.table;

import gr.alfoks.popularmovies.BuildConfig;

import android.content.ContentResolver;
import android.net.Uri;


import static gr.alfoks.popularmovies.data.Constants.CONTENT_SCHEME;

public class MoviesTable {
    public static final String NAME = "Movies";

    public static final class Columns {
        public static final String ID = "movieId";
        public static final String TITLE = "title";
        public static final String ORIGINAL_TITLE = "originalTitle";
        public static final String POSTER_PATH = "posterPath";
        public static final String OVERVIEW = "overview";
        public static final String VOTE_AVERAGE = "voteAverage";
        /**
         * Stored as milliseconds since 01/01/1970 00:00:00 GMT
         */
        public static final String RELEASE_DATE = "releaseDate";
        public static final String RUNTIME = "runtime";
        public static final String FAVORITE = "favorite";
        /**
         * Page as returned from TMDB
         */
        public static final String PAGE = "tmdbPage";
        /**
         * Order as returned from TMDB within a page
         */
        public static final String ORDER = "tmdbOrder";
    }

    public static final class Content {
        public static final String CONTENT_AUTHORITY = BuildConfig.CONTENT_AUTHORITY;
        public static final String PATH_MOVIE = "movie";

        private static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + CONTENT_AUTHORITY);
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
            .buildUpon()
            .appendPath(PATH_MOVIE)
            .build();

        public static final String TYPE_DIR =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String TYPE_ITEM =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
    }

    public class Ddl {
        public static final String CREATE_TABLE = "CREATE TABLE " + NAME + " (" +
            Columns.ID + " INTEGER PRIMARY KEY," +
            Columns.TITLE + " TEXT," +
            Columns.ORIGINAL_TITLE + " TEXT," +
            Columns.POSTER_PATH + " TEXT," +
            Columns.OVERVIEW + " TEXT," +
            Columns.VOTE_AVERAGE + " REAL," +
            Columns.RELEASE_DATE + " DATETIME," +
            Columns.RUNTIME + " INTEGER," +
            Columns.FAVORITE + " INTEGER DEFAULT 0," +
            Columns.PAGE + " INTEGER DEFAULT 0," +
            Columns.ORDER + " INTEGER DEFAULT 0" +
            ");";
    }
}
