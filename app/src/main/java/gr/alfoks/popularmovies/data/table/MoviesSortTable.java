package gr.alfoks.popularmovies.data.table;

import android.net.Uri;


import static gr.alfoks.popularmovies.data.Constants.BASE_CONTENT_URI;

public final class MoviesSortTable {
    public static final String NAME = "MoviesSort";

    public static final class Columns {
        public static final String MOVIE_ID = "movieId";
        public static final String SORT_TYPE = "sortType";
        public static final String SORT_ORDER = "sortOrder";
    }

    public static final class Content {
        public static final String PATH_MOVIES_SORT = "movies-sort";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
            .buildUpon()
            .appendPath(PATH_MOVIES_SORT)
            .build();
    }

    public static final class Ddl {
        private static final String PRIMARY_KEY =
            "PRIMARY KEY(" +
                Columns.MOVIE_ID + "," +
                Columns.SORT_TYPE +
                ")";

        private static final String FOREIGN_KEY =
            "REFERENCES " + MoviesTable.NAME + "(" + MoviesTable.Columns.ID + ") " +
                " ON DELETE CASCADE";

        public static final String CREATE_TABLE = "CREATE TABLE " + NAME + " (" +
            Columns.MOVIE_ID + " INTEGER " + FOREIGN_KEY + "," +
            Columns.SORT_TYPE + " INTEGER," +
            Columns.SORT_ORDER + " INTEGER," +
            PRIMARY_KEY +
            ");";
    }
}
