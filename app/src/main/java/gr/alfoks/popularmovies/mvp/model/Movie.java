package gr.alfoks.popularmovies.mvp.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import gr.alfoks.popularmovies.data.table.MoviesTable;

import android.content.ContentValues;
import android.database.Cursor;

public class Movie implements Serializable {
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
    public final int page;
    public final int order;

    Movie(
        long id,
        String title,
        String originalTitle,
        String posterPath,
        String overview,
        float voteAverage,
        Date releaseDate,
        int runtime,
        boolean favorite,
        int page,
        int order) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.favorite = favorite;
        this.page = page;
        this.order = order;
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
        values.put(MoviesTable.Columns.FAVORITE, favorite);
        values.put(MoviesTable.Columns.PAGE, page);
        values.put(MoviesTable.Columns.ORDER, order);

        return values;
    }

    public static Movie create(Cursor c) {
        long id = c.getLong(c.getColumnIndex(MoviesTable.Columns.ID));
        String title = c.getString(c.getColumnIndex(MoviesTable.Columns.TITLE));
        String originalTitle = c.getString(c.getColumnIndex(MoviesTable.Columns.ORIGINAL_TITLE));
        String overview = c.getString(c.getColumnIndex(MoviesTable.Columns.OVERVIEW));
        String posterPath = c.getString(c.getColumnIndex(MoviesTable.Columns.POSTER_PATH));
        float voteAverage = c.getFloat(c.getColumnIndex(MoviesTable.Columns.VOTE_AVERAGE));
        long date = c.getLong(c.getColumnIndex(MoviesTable.Columns.RELEASE_DATE));
        Date releaseDate = new Date(date);
        int runtime = c.getInt(c.getColumnIndex(MoviesTable.Columns.RUNTIME));
        boolean favorite = c.getInt(c.getColumnIndex(MoviesTable.Columns.FAVORITE)) == 1;
        int page = c.getInt(c.getColumnIndex(MoviesTable.Columns.PAGE));
        int order = c.getInt(c.getColumnIndex(MoviesTable.Columns.ORDER));

        return new MovieBuilder()
            .setId(id)
            .setTitle(title)
            .setOriginalTitle(originalTitle)
            .setPosterPath(posterPath)
            .setOverview(overview)
            .setVoteAverage(voteAverage)
            .setReleaseDate(releaseDate)
            .setRuntime(runtime)
            .setFavorite(favorite)
            .setPage(page)
            .setOrder(order)
            .build();
    }
}
