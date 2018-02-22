package gr.alfoks.popularmovies.util;

import gr.alfoks.popularmovies.model.Movies;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbApi {
    enum SortBy {
        POPULAR("popular"),
        TOP_RATED("top_rated");

        private final String value;

        SortBy(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    String BASE_URL = "https://api.themoviedb.org/";
    String API_PATH = "/3/movie";

    @GET(API_PATH + "/{sort-by}")
    Single<Movies> getMovies(@Path("sort-by") SortBy sortBy, @Query("page") int page);
}
