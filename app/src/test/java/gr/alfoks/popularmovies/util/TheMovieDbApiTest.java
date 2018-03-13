package gr.alfoks.popularmovies.util;

import java.io.IOException;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.testutil.MoviesResult;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static gr.alfoks.popularmovies.testutil.DateUtils.assertDateEquals;
import static gr.alfoks.popularmovies.testutil.MoviesResult.getTestObserver;
import static org.junit.Assert.assertEquals;

public class TheMovieDbApiTest {
    private MockWebServer server;
    private TheMovieDbApi api;

    @Before
    public void init() throws IOException {
        server = new MockWebServer();
        server.start();

        api = new RestClient<>(
            server.url("/").toString(),
            TheMovieDbApi.class,
            null,
            null
        ).create();
    }

    @After
    public void cleanUp() throws IOException {
        server.shutdown();
    }

    private static final String NO_MOVIES_JSON_RESULT = String.join("",
        "{\"page\":1,\"total_results\":0,\"total_pages\":1,\"results\":[]}");

    private static final String ONE_MOVIE_ALL_FIELDS_JSON_RESULT = String.join("",
        "{\"page\":1,\"total_results\":1,\"total_pages\":1,\"results\":[",
        "{\"vote_count\":1,\"id\":1,\"video\":false,\"vote_average\":1.1,\"title\":\"title1\",",
        "\"popularity\":100.100100,\"poster_path\":\"\\/poster1.jpg\",",
        "\"original_language\":\"en\",\"original_title\":\"original title1\",\"genre_ids\":[1,11,111],",
        "\"backdrop_path\":\"\\/backdrop1.jpg\",\"adult\":false,",
        "\"overview\":\"overview1\",\"release_date\":\"2001-01-01\"}",
        "]}");

    private static final String TWO_MOVIES_JSON_RESULT = String.join("",
        "{\"page\":1,\"total_results\":2,\"total_pages\":1,\"results\":[",
        "{\"vote_average\":1.1,\"title\":\"title1\",",
        "\"poster_path\":\"\\/poster1.jpg\",",
        "\"original_title\":\"original title1\",",
        "\"overview\":\"overview1\",\"release_date\":\"2001-01-01\"},",
        "{\"vote_average\":2.2,\"title\":\"title2\",",
        "\"poster_path\":\"\\/poster2.jpg\",",
        "\"original_title\":\"original title2\",",
        "\"overview\":\"overview2\",\"release_date\":\"2002-02-02\"}",
        "]}");

    @Test
    public void testGetMoviesPathReplacedCorrectly() throws Exception {
        server.enqueue(new MockResponse().setBody(""));
        server.enqueue(new MockResponse().setBody(""));

        api.getMovies(SortBy.POPULAR, 1).subscribe(new TestObserver<Movies>());
        api.getMovies(SortBy.TOP_RATED, 1).subscribe(new TestObserver<Movies>());

        RecordedRequest request = server.takeRequest();
        assertEquals(TheMovieDbApi.API_PATH + "/popular?page=1", request.getPath());

        request = server.takeRequest();
        assertEquals(TheMovieDbApi.API_PATH + "/top_rated?page=1", request.getPath());
    }

    @Test
    public void testGetMoviesErrorResponse() throws Exception {
        int code = 401;
        String message = "{\"status_message\": \"error\", \"status_code\": 401 }";

        server.enqueue(new MockResponse().setResponseCode(code).setBody(message));

        MoviesResult moviesResult = new MoviesResult();
        final Single<Movies> moviesObservable = api.getMovies(SortBy.POPULAR, 1);
        TestObserver observer = moviesObservable.subscribeWith(getTestObserver(moviesResult));

        observer.assertNotComplete();
        assertEquals(null, moviesResult.getMovies());
        assertEquals(code, moviesResult.getHttpStatusCode());
        assertEquals(message, moviesResult.getMessage());
    }

    @Test
    public void testGetMoviesEmptyResponse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(NO_MOVIES_JSON_RESULT));

        MoviesResult moviesResult = new MoviesResult();
        final Single<Movies> moviesObservable = api.getMovies(SortBy.POPULAR, 1);
        moviesObservable.subscribeWith(getTestObserver(moviesResult));

        assertEquals(0, moviesResult.getMovies().getMovies().size());
    }

    @Test
    public void testGetMoviesOneResult() throws Exception {
        server.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(ONE_MOVIE_ALL_FIELDS_JSON_RESULT));

        final Single<Movies> moviesObservable = api.getMovies(SortBy.POPULAR, 1);

        final MoviesResult moviesResult = new MoviesResult();
        TestObserver observer = moviesObservable.subscribeWith(getTestObserver(moviesResult));

        observer.assertComplete();
        Movies movies = moviesResult.getMovies();
        assertEquals(1, movies.getMovies().size());

        Movie movie = movies.getMovies().get(0);
        assertEquals("title1", movie.title);
        assertEquals("original title1", movie.originalTitle);
        assertEquals("/poster1.jpg", movie.posterPath);
        assertEquals("overview1", movie.overview);
        assertEquals(1.1, movie.voteAverage, 0.001);
        assertDateEquals(movie.releaseDate, 1, 1, 2001);
    }

    @Test
    public void testGetMoviesTwoResults() throws Exception {
        server.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(TWO_MOVIES_JSON_RESULT));

        final Single<Movies> moviesObservable = api.getMovies(SortBy.POPULAR, 1);

        final MoviesResult moviesResult = new MoviesResult();
        TestObserver observer = moviesObservable.subscribeWith(getTestObserver(moviesResult));

        observer.assertComplete();
        Movies movies = moviesResult.getMovies();
        assertEquals(2, movies.getMovies().size());

        Movie movie = movies.getMovies().get(0);
        assertEquals("title1", movie.title);
        assertEquals("original title1", movie.originalTitle);
        assertEquals("/poster1.jpg", movie.posterPath);
        assertEquals("overview1", movie.overview);
        assertEquals(1.1, movie.voteAverage, 0.001);
        assertDateEquals(movie.releaseDate, 1, 1, 2001);

        movie = movies.getMovies().get(1);
        assertEquals("title2", movie.title);
        assertEquals("original title2", movie.originalTitle);
        assertEquals("/poster2.jpg", movie.posterPath);
        assertEquals("overview2", movie.overview);
        assertEquals(2.2, movie.voteAverage, 0.001);
        assertDateEquals(movie.releaseDate, 2, 2, 2002);
    }
}
