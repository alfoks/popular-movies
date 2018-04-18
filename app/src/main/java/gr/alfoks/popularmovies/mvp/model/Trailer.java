package gr.alfoks.popularmovies.mvp.model;

import gr.alfoks.popularmovies.data.table.TrailersTable;

import android.content.ContentValues;
import android.database.Cursor;

public class Trailer {
    private static final String YOUTUBE_SITE = "YouTube";
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    /**
     * This url is undocumented (I think). Found out that it works while
     * experimenting with YouTube API. This link returns the video thumbnail in
     * standard resolution (640x480)
     **/
    private static final String YOUTUBE_THUMBNAIL_URL = "https://i.ytimg.com/vi/%s/sddefault.jpg";

    public final String id;
    public final String key;
    public final String site;

    private Trailer(TrailerBuilder builder) {
        this.id = builder.id;
        this.key = builder.key;
        this.site = builder.site;
    }

    public static TrailerBuilder builder() {
        return new TrailerBuilder();
    }

    public String getUrl() {
        if(site.equals(YOUTUBE_SITE)) {
            return String.format(YOUTUBE_URL, key);
        }

        return "";
    }

    public String getThumbnailUrl() {
        return String.format(YOUTUBE_THUMBNAIL_URL, key);
    }

    public ContentValues asValues(long movieId) {
        ContentValues values = new ContentValues();
        values.put(TrailersTable.Columns.ID, id);
        values.put(TrailersTable.Columns.MOVIE_ID, movieId);
        values.put(TrailersTable.Columns.KEY, key);
        values.put(TrailersTable.Columns.SITE, site);

        return values;
    }

    public static class TrailerBuilder {
        private String id;
        private String key;
        private String site;

        public TrailerBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public TrailerBuilder setKey(String key) {
            this.key = key;
            return this;
        }

        public TrailerBuilder setSite(String site) {
            this.site = site;
            return this;
        }

        public Trailer build() {
            return new Trailer(this);
        }

        public Trailer.TrailerBuilder from(Cursor c) {
            setId(c.getString(c.getColumnIndex(TrailersTable.Columns.ID)));
            setKey(c.getString(c.getColumnIndex(TrailersTable.Columns.KEY)));
            setSite(c.getString(c.getColumnIndex(TrailersTable.Columns.SITE)));

            return this;
        }
    }

}
