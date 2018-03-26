package gr.alfoks.popularmovies.mvp.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import gr.alfoks.popularmovies.data.table.MoviesTable;

import android.content.ContentValues;
import android.database.Cursor;

public class Movie implements Serializable {
    private static final long EMPTY_ID = -1;
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";

    public final long id;
    public final String title;
    @SerializedName("original_title")
    public final String originalTitle;
    @SerializedName("poster_path")
    public final String posterPath;
    public final String overview;
    @SerializedName("vote_average")
    public final float voteAverage;
    @SerializedName("release_date")
    public final Date releaseDate;
    public final int runtime;
    public final boolean favorite;

    private Movie(MovieBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.originalTitle = builder.originalTitle;
        this.posterPath = builder.posterPath;
        this.overview = builder.overview;
        this.voteAverage = builder.voteAverage;
        this.releaseDate = builder.releaseDate;
        this.runtime = builder.runtime;
        this.favorite = builder.favorite;
    }

    public static MovieBuilder builder() {
        return new MovieBuilder();
    }

    public boolean isEmpty() {
        return id == EMPTY_ID;
    }

    public String getFullPosterPath() {
        return POSTER_BASE_URL + posterPath;
    }

    public int getReleaseYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(releaseDate);
        return calendar.get(Calendar.YEAR);
    }

    public String getDuration() {
        int hours = runtime / 60;
        int minutes = runtime - (hours * 60);

        StringBuilder sb = new StringBuilder();
        if(hours > 0) {
            sb.append(hours).append(":");
        }
        if(minutes > 0) {
            sb.append(minutes);
        }

        return sb.toString();
    }

    public String getRating() {
        return voteAverage + "/10";
    }

    public ContentValues asValues() {
        ContentValues values = new ContentValues();
        values.put(MoviesTable.Columns.ID, id);
        values.put(MoviesTable.Columns.TITLE, title);
        values.put(MoviesTable.Columns.ORIGINAL_TITLE, originalTitle);
        values.put(MoviesTable.Columns.OVERVIEW, overview);
        values.put(MoviesTable.Columns.POSTER_PATH, posterPath);
        values.put(MoviesTable.Columns.VOTE_AVERAGE, voteAverage);
        values.put(MoviesTable.Columns.RELEASE_DATE, releaseDate.getTime());
        values.put(MoviesTable.Columns.RUNTIME, runtime);
        values.put(MoviesTable.Columns.FAVORITE, favorite ? 1 : 0);

        return values;
    }

    public static class MovieBuilder {
        private long id = EMPTY_ID;
        private String title;
        private String originalTitle;
        private String posterPath;
        private String overview;
        private float voteAverage;
        private Date releaseDate = new Date();
        private int runtime;
        private boolean favorite = false;

        public MovieBuilder setId(long id) {
            this.id = id;
            return this;
        }

        public MovieBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public MovieBuilder setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }

        public MovieBuilder setPosterPath(String posterPath) {
            this.posterPath = posterPath;
            return this;
        }

        public MovieBuilder setOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public MovieBuilder setVoteAverage(float voteAverage) {
            this.voteAverage = voteAverage;
            return this;
        }

        public MovieBuilder setReleaseDate(Date releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public MovieBuilder setRuntime(int runtime) {
            this.runtime = runtime;
            return this;
        }

        public MovieBuilder setFavorite(boolean favorite) {
            this.favorite = favorite;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }

        public MovieBuilder from(Movie movie) {
            setId(movie.id);
            setTitle(movie.title);
            setOriginalTitle(movie.originalTitle);
            setOverview(movie.overview);
            setPosterPath(movie.posterPath);
            setVoteAverage(movie.voteAverage);
            setReleaseDate(movie.releaseDate);
            setRuntime(movie.runtime);

            return this;
        }

        public MovieBuilder from(Cursor c) {
            setId(c.getLong(c.getColumnIndex(MoviesTable.Columns.ID)));
            setTitle(c.getString(c.getColumnIndex(MoviesTable.Columns.TITLE)));
            setOriginalTitle(c.getString(c.getColumnIndex(MoviesTable.Columns.ORIGINAL_TITLE)));
            setOverview(c.getString(c.getColumnIndex(MoviesTable.Columns.OVERVIEW)));
            setPosterPath(c.getString(c.getColumnIndex(MoviesTable.Columns.POSTER_PATH)));
            setVoteAverage(c.getFloat(c.getColumnIndex(MoviesTable.Columns.VOTE_AVERAGE)));
            long date = c.getLong(c.getColumnIndex(MoviesTable.Columns.RELEASE_DATE));
            setReleaseDate(new Date(date));
            setRuntime(c.getInt(c.getColumnIndex(MoviesTable.Columns.RUNTIME)));
            setFavorite(c.getInt(c.getColumnIndex(MoviesTable.Columns.FAVORITE)) == 1);

            return this;
        }
    }
}
