package gr.alfoks.popularmovies.mvp.model;

import gr.alfoks.popularmovies.data.table.TrailersTable;

import android.content.ContentValues;

public class Trailer {
    private static final String YOUTUBE_SITE = "YouTube";
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    public final String id;
    public final String key;
    public final String site;

    public Trailer(String id, String key, String site) {
        this.id = id;
        this.key = key;
        this.site = site;
    }

    public String getUrl() {
        if(site.equals(YOUTUBE_SITE)) {
            return String.format(YOUTUBE_URL, key);
        }

        return "";
    }

    public ContentValues asValues(long movieId) {
        ContentValues values = new ContentValues();
        values.put(TrailersTable.Columns.ID, id);
        values.put(TrailersTable.Columns.MOVIE_ID, movieId);
        values.put(TrailersTable.Columns.KEY, key);
        values.put(TrailersTable.Columns.SITE, site);

        return values;
    }
}
