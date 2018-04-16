package gr.alfoks.popularmovies.mvp.model;

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
}
