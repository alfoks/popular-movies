package gr.alfoks.popularmovies.data.table;

import android.net.Uri;


import static gr.alfoks.popularmovies.data.Constants.BASE_CONTENT_URI;

public final class TrailersTable {
    public static final String NAME = "Trailers";

    public static final class Columns {
        public static final String ID = "id";
        public static final String MOVIE_ID = "movieId";
        public static final String KEY = "key";
        public static final String SITE = "site";
    }

    public static final class Content {
        public static final String PATH_TRAILERS = "trailers";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
            .buildUpon()
            .appendPath(PATH_TRAILERS)
            .build();
    }

    public static final class Ddl {
        private static final String FOREIGN_KEY =
            "REFERENCES " + MoviesTable.NAME + "(" + MoviesTable.Columns.ID + ") " +
                " ON DELETE CASCADE";

        public static final String CREATE_TABLE = "CREATE TABLE " + NAME + " (" +
            Columns.ID + " TEXT PRIMARY KEY," +
            Columns.MOVIE_ID + " INTEGER " + FOREIGN_KEY + "," +
            Columns.KEY + " TEXT," +
            Columns.SITE + " TEXT);";
    }
}
