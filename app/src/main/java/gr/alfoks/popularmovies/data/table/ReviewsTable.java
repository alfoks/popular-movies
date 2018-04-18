package gr.alfoks.popularmovies.data.table;

import android.net.Uri;


import static gr.alfoks.popularmovies.data.Constants.BASE_CONTENT_URI;

public final class ReviewsTable {
    public static final String NAME = "Reviews";

    public static final class Columns {
        public static final String ID = "id";
        public static final String MOVIE_ID = "movieId";
        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";
    }

    public static final class Content {
        public static final String PATH_REVIEWS = "Reviews";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
            .buildUpon()
            .appendPath(PATH_REVIEWS)
            .build();
    }

    public static final class Ddl {
        private static final String FOREIGN_KEY =
            "REFERENCES " + MoviesTable.NAME + "(" + MoviesTable.Columns.ID + ") " +
                " ON DELETE CASCADE";

        public static final String CREATE_TABLE = "CREATE TABLE " + NAME + " (" +
            Columns.ID + " TEXT PRIMARY KEY," +
            Columns.MOVIE_ID + " INTEGER " + FOREIGN_KEY + "," +
            Columns.AUTHOR + " TEXT," +
            Columns.CONTENT + " TEXT);";
    }
}
