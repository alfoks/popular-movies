package gr.alfoks.popularmovies.data;

import java.util.Date;

import gr.alfoks.popularmovies.data.table.MoviesSortTable;
import gr.alfoks.popularmovies.data.table.MoviesTable;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.testutil.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static gr.alfoks.popularmovies.testutil.Utils.assertMoviesEqual;
import static gr.alfoks.popularmovies.testutil.Utils.assertMoviesEqualNoId;

@RunWith(AndroidJUnit4.class)
public class MoviesProviderTest extends ProviderTestCase2<MoviesProvider> {
    private static final String NON_MATCHING_TITLE = "NonMatchingTitle";

    private final static Movie movie1 = Movie
        .builder()
        .setId(100)
        .setTitle("title1")
        .setOriginalTitle("original title1")
        .setPosterPath("poster1.jpg")
        .setOverview("overview1")
        .setVoteAverage(1.1f)
        .setReleaseDate(new Date(1))
        .setRuntime(111)
        .setFavorite(false)
        .build();

    private final static ContentValues sortOrder1 = new ContentValues();

    static {
        sortOrder1.put(MoviesSortTable.Columns.MOVIE_ID, movie1.id);
        sortOrder1.put(MoviesSortTable.Columns.SORT_ORDER, 11);
        sortOrder1.put(MoviesSortTable.Columns.SORT_TYPE, 111);
    }

    private final Movie movie1Updated = Movie
        .builder()
        .setId(100)
        .setTitle("title1u")
        .setOriginalTitle("original title1u")
        .setPosterPath("poster1u.jpg")
        .setOverview("overview1u")
        .setVoteAverage(1.11f)
        .setReleaseDate(new Date(11))
        .setRuntime(1111)
        .setFavorite(true)
        .build();

    private final static Movie movie2 = Movie
        .builder()
        .setId(200)
        .setTitle("title2")
        .setOriginalTitle("original title2")
        .setPosterPath("poster2.jpg")
        .setOverview("overview2")
        .setVoteAverage(2.2f)
        .setReleaseDate(new Date(2))
        .setRuntime(222)
        .setFavorite(false)
        .build();

    private final static ContentValues sortOrder2 = new ContentValues();

    static {
        sortOrder2.put(MoviesSortTable.Columns.MOVIE_ID, movie2.id);
        sortOrder2.put(MoviesSortTable.Columns.SORT_ORDER, 22);
        sortOrder2.put(MoviesSortTable.Columns.SORT_TYPE, 222);
    }

    public MoviesProviderTest() {
        super(MoviesProvider.class, MoviesTable.Content.CONTENT_AUTHORITY);
    }

    @Before
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @Test
    public void testInsertEmptyOrNull() {
        Utils.assertExceptionIsThrown(
            () -> getMockContentResolver().insert(MoviesTable.Content.CONTENT_URI, null),
            IllegalArgumentException.class
        );

        Utils.assertExceptionIsThrown(
            () -> getMockContentResolver().insert(MoviesTable.Content.CONTENT_URI, new ContentValues()),
            IllegalArgumentException.class
        );
    }

    @Test
    public void testInsertInvalidUris() {
        Utils.assertExceptionIsThrown(
            () -> {
                Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, 1);
                getMockContentResolver().insert(uri, null);
            },
            UnsupportedOperationException.class
        );

        Utils.assertExceptionIsThrown(
            () -> {
                Uri unsupportedUri = MoviesTable.Content.CONTENT_URI.buildUpon().appendPath("unsupported").build();
                getMockContentResolver().insert(unsupportedUri, new ContentValues());
            },
            UnsupportedOperationException.class
        );

    }

    @Test
    public void testInsert() {
        Uri uri = getMockContentResolver().insert(MoviesTable.Content.CONTENT_URI, movie1.asValues());
        assertEquals(ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id), uri);

        uri = getMockContentResolver().insert(MoviesTable.Content.CONTENT_URI, movie2.asValues());
        assertEquals(ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie2.id), uri);
    }

    @Test
    public void testInsertExisting() {
        Uri uri = getMockContentResolver().insert(MoviesTable.Content.CONTENT_URI, movie1.asValues());
        assertEquals(ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id), uri);

        uri = getMockContentResolver().insert(MoviesTable.Content.CONTENT_URI, movie1Updated.asValues());
        assertEquals(ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id), uri);
    }

    @Test
    public void testQueryInvalidUris() {
        Utils.assertExceptionIsThrown(
            () -> {
                Uri unsupportedUri = MoviesTable.Content.CONTENT_URI.buildUpon().appendPath("unsupported").build();
                getMockContentResolver().query(unsupportedUri, null, null, null, null);
            },
            UnsupportedOperationException.class
        );
    }

    @Test
    public void testQueryOne() {
        insertMoviesToDb();

        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        Cursor c = getMockContentResolver().query(uri, null, null, null, null);

        assertTrue(c != null);
        assertEquals(1, c.getCount());
        c.moveToFirst();
        assertMoviesEqual(movie1, Movie.builder().from(c).build());
        c.close();
    }

    @Test
    public void testQueryOneNoData() {
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        Cursor c = getMockContentResolver().query(uri, null, null, null, null);

        assertTrue(c != null);
        assertEquals(0, c.getCount());
        c.close();
    }

    @Test
    public void testQueryOneWithMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { movie1.title };
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        Cursor c = getMockContentResolver().query(uri, null, selection, selectionArgs, null);

        assertTrue(c != null);
        assertEquals(1, c.getCount());
        c.moveToFirst();
        assertMoviesEqual(movie1, Movie.builder().from(c).build());
        c.close();
    }

    @Test
    public void testQueryOneWithNonMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { NON_MATCHING_TITLE };
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        Cursor c = getMockContentResolver().query(uri, null, selection, selectionArgs, null);

        assertTrue(c != null);
        assertEquals(0, c.getCount());
        c.close();
    }

    @Test
    public void testQueryAll() {
        insertMoviesToDb();

        Uri uri = MoviesTable.Content.CONTENT_URI;
        Cursor c = getMockContentResolver().query(uri, null, null, null, MoviesTable.Columns.ID);

        assertTrue(c != null);
        assertEquals(2, c.getCount());
        c.moveToFirst();
        assertMoviesEqual(movie1, Movie.builder().from(c).build());
        c.moveToNext();
        assertMoviesEqual(movie2, Movie.builder().from(c).build());
        c.close();
    }

    @Test
    public void testQueryAllWithMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { movie2.title };
        Uri uri = MoviesTable.Content.CONTENT_URI;
        Cursor c = getMockContentResolver().query(uri, null, selection, selectionArgs, MoviesTable.Columns.ID);

        assertTrue(c != null);
        assertEquals(1, c.getCount());
        c.moveToFirst();
        assertMoviesEqual(movie2, Movie.builder().from(c).build());
        c.close();
    }

    @Test
    public void testQueryAllWithNonMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { NON_MATCHING_TITLE };
        Uri uri = MoviesTable.Content.CONTENT_URI;
        Cursor c = getMockContentResolver().query(uri, null, selection, selectionArgs, MoviesTable.Columns.ID);

        assertTrue(c != null);
        assertEquals(0, c.getCount());
        c.close();
    }

    @Test
    public void testUpdateEmptyOrNull() {
        Utils.assertExceptionIsThrown(
            () -> getMockContentResolver().update(MoviesTable.Content.CONTENT_URI, null, null, null),
            IllegalArgumentException.class
        );

        Utils.assertExceptionIsThrown(
            () -> getMockContentResolver().update(MoviesTable.Content.CONTENT_URI, new ContentValues(), null, null),
            IllegalArgumentException.class
        );
    }

    @Test
    public void testUpdateInvalidUris() {
        Utils.assertExceptionIsThrown(
            () -> {
                Uri unsupportedUri = MoviesTable.Content.CONTENT_URI.buildUpon().appendPath("unsupported").build();
                getMockContentResolver().update(unsupportedUri, null, null, null);
            },
            UnsupportedOperationException.class
        );
    }

    @Test
    public void testUpdateOne() {
        insertMoviesToDb();

        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        int numUpdated = getMockContentResolver().update(uri, movie1Updated.asValues(), null, null);

        assertEquals(1, numUpdated);
        assertMoviesEqual(movie1Updated, getMovieFromDb(movie1.id));
    }

    @Test
    public void testUpdateOneWithMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { movie1.title };
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        int numUpdated = getMockContentResolver().update(uri, movie1Updated.asValues(), selection, selectionArgs);

        assertEquals(1, numUpdated);
        assertMoviesEqual(movie1Updated, getMovieFromDb(movie1.id));
    }

    @Test
    public void testUpdateOneWithNonMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { NON_MATCHING_TITLE };
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        int numUpdated = getMockContentResolver().update(uri, movie1Updated.asValues(), selection, selectionArgs);

        assertEquals(0, numUpdated);
    }

    @Test
    public void testUpdateAll() {
        insertMoviesToDb();

        Uri uri = MoviesTable.Content.CONTENT_URI;
        ContentValues values = movie1Updated.asValues();
        values.remove(MoviesTable.Columns.ID);
        int numUpdated = getMockContentResolver().update(uri, values, null, null);

        assertEquals(2, numUpdated);
        assertMoviesEqualNoId(movie1Updated, getMovieFromDb(movie1.id));
        assertMoviesEqualNoId(movie1Updated, getMovieFromDb(movie2.id));
    }

    @Test
    public void testUpdateAllWithMatchingCriteria() {
        insertMoviesToDb();

        Uri uri = MoviesTable.Content.CONTENT_URI;
        ContentValues values = movie1Updated.asValues();
        values.remove(MoviesTable.Columns.ID);
        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { movie2.title };
        int numUpdated = getMockContentResolver().update(uri, values, selection, selectionArgs);

        assertEquals(1, numUpdated);
        assertMoviesEqualNoId(movie1Updated, getMovieFromDb(movie2.id));
    }

    @Test
    public void testUpdateAllWithNonMatchingCriteria() {
        insertMoviesToDb();

        Uri uri = MoviesTable.Content.CONTENT_URI;
        ContentValues values = movie1Updated.asValues();
        values.remove(MoviesTable.Columns.ID);
        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { NON_MATCHING_TITLE };
        int numUpdated = getMockContentResolver().update(uri, values, selection, selectionArgs);

        assertEquals(0, numUpdated);
    }

    @Test
    public void testDeleteInvalidUris() {
        Utils.assertExceptionIsThrown(
            () -> {
                Uri unsupportedUri = MoviesTable.Content.CONTENT_URI.buildUpon().appendPath("unsupported").build();
                getMockContentResolver().delete(unsupportedUri, null, null);
            },
            UnsupportedOperationException.class
        );
    }

    @Test
    public void testDeleteOne() {
        insertMoviesToDb();

        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie1.id);
        int numDeleted = getMockContentResolver().delete(uri, null, null);

        assertEquals(1, numDeleted);

        //Passed, movie doesn't exist
        Utils.assertExceptionIsThrown(
            () -> getMovieFromDb(movie1.id),
            CursorIndexOutOfBoundsException.class
        );

        //Passed, movie exists
        getMovieFromDb(movie2.id);
    }

    @Test
    public void testDeleteOneWithMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { movie2.title };
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie2.id);
        int numDeleted = getMockContentResolver().delete(uri, selection, selectionArgs);

        assertEquals(1, numDeleted);

        //Passed, movie exists
        getMovieFromDb(movie1.id);

        //Passed, movie doesn't exist
        Utils.assertExceptionIsThrown(
            () -> getMovieFromDb(movie2.id),
            CursorIndexOutOfBoundsException.class
        );
    }

    @Test
    public void testDeleteOneWithNonMatchingCriteria() {
        insertMoviesToDb();

        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { NON_MATCHING_TITLE };
        Uri uri = ContentUtils.withAppendedId(MoviesTable.Content.CONTENT_URI, movie2.id);
        int numDeleted = getMockContentResolver().delete(uri, selection, selectionArgs);

        assertEquals(0, numDeleted);
        //Passed, movie exists
        getMovieFromDb(movie1.id);
        //Passed, movie exists
        getMovieFromDb(movie2.id);
    }

    @Test
    public void testDeleteAll() {
        insertMoviesToDb();

        Uri uri = MoviesTable.Content.CONTENT_URI;
        int numDeleted = getMockContentResolver().delete(uri, null, null);

        assertEquals(2, numDeleted);

        //Passed, movie doesn't exist
        Utils.assertExceptionIsThrown(
            () -> getMovieFromDb(movie1.id),
            CursorIndexOutOfBoundsException.class
        );

        //Passed, movie doesn't exist
        Utils.assertExceptionIsThrown(
            () -> getMovieFromDb(movie2.id),
            CursorIndexOutOfBoundsException.class
        );
    }

    @Test
    public void testDeleteAllWithMatchingCriteria() {
        insertMoviesToDb();

        Uri uri = MoviesTable.Content.CONTENT_URI;
        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { movie2.title };
        int numDeleted = getMockContentResolver().delete(uri, selection, selectionArgs);

        assertEquals(1, numDeleted);

        //Passed, movie exists
        getMovieFromDb(movie1.id);

        //Passed, movie doesn't exist
        Utils.assertExceptionIsThrown(
            () -> getMovieFromDb(movie2.id),
            CursorIndexOutOfBoundsException.class
        );
    }

    @Test
    public void testDeleteAllWithNonMatchingCriteria() {
        insertMoviesToDb();

        Uri uri = MoviesTable.Content.CONTENT_URI;
        String selection = MoviesTable.Columns.TITLE + "=?";
        String[] selectionArgs = new String[] { NON_MATCHING_TITLE };
        int numDeleted = getMockContentResolver().delete(uri, selection, selectionArgs);

        assertEquals(0, numDeleted);
        //Passed, movie exists
        getMovieFromDb(movie1.id);
        //Passed, movie exists
        getMovieFromDb(movie2.id);
    }

    private void insertMoviesToDb() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(getMockContext());
        dbHelper.getWritableDatabase().insert(MoviesTable.NAME, null, movie1.asValues());
        dbHelper.getWritableDatabase().insert(MoviesSortTable.NAME, null, sortOrder1);

        dbHelper.getWritableDatabase().insert(MoviesTable.NAME, null, movie2.asValues());
        dbHelper.getWritableDatabase().insert(MoviesSortTable.NAME, null, sortOrder2);
        dbHelper.close();
    }

    private Movie getMovieFromDb(long movieId) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(getMockContext());
        String selection = MoviesTable.Columns.ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(movieId) };

        Cursor c = dbHelper.getWritableDatabase().query(
            MoviesTable.NAME_FOR_JOIN,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        );

        c.moveToFirst();
        Movie movie = Movie.builder().from(c).build();
        c.close();

        return movie;
    }
}
