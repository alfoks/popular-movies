package gr.alfoks.popularmovies.mvp.model;

import java.io.Serializable;

public enum SortBy implements Serializable {
    POPULAR("popular", "Popular"),
    TOP_RATED("top_rated", "Top rated"),
    NOW_PLAYING("now_playing", "Now playing"),
    UPCOMING("upcoming", "Upcoming");

    private final String value;
    private final String displayName;

    SortBy(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return value;
    }
}
