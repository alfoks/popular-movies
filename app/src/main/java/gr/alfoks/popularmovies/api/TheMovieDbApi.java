package gr.alfoks.popularmovies.api;

import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbApi {
    String BASE_URL = "https://api.themoviedb.org/";
    String API_PATH = "/3/movie";

    @GET(API_PATH + "/{movie-id}?append_to_response=videos")
    Single<Movie> loadMovie(@Path("movie-id") long movieId);

    @GET(API_PATH + "/{sort-by}")
    Single<Movies> loadMovies(@Path("sort-by") SortBy sortBy, @Query("page") int page);
}
