package gr.alfoks.popularmovies.testutil;

import gr.alfoks.popularmovies.mvp.model.Movie;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Utils {
    public static void assertMoviesEqual(Movie expected, Movie actual) {
        assertEquals(expected.id, actual.id);
        assertMoviesEqualNoId(expected, actual);
    }

    public static void assertMoviesEqualNoId(Movie expected, Movie actual) {
        assertEquals(expected.title, actual.title);
        assertEquals(expected.originalTitle, actual.originalTitle);
        assertEquals(expected.posterPath, actual.posterPath);
        assertEquals(expected.overview, actual.overview);
        assertEquals(expected.voteAverage, actual.voteAverage, 0.001f);
        assertEquals(expected.releaseDate, actual.releaseDate);
        assertEquals(expected.runtime, actual.runtime);
    }

    public static <T extends Exception> void assertExceptionIsThrown(Runnable runnable, Class<T> exceptionClass) {
        try {
            runnable.run();
            fail("Should have thrown " + exceptionClass.getSimpleName());
        } catch(Exception ex) {
            //Passed, thrown as expected
            assertEquals(exceptionClass, ex.getClass());
        }
    }
}
