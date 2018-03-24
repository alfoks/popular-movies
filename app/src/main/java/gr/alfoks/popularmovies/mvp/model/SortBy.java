package gr.alfoks.popularmovies.mvp.model;

import java.io.Serializable;
import java.util.HashMap;

import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.util.ResourceUtils;

import android.content.Context;
import android.util.Log;

public enum SortBy implements Serializable {
    POPULAR(1, "popular"),
    TOP_RATED(2, "top_rated"),
    FAVORITES(3, "favorites"),
    NOW_PLAYING(4, "now_playing"),
    UPCOMING(5, "upcoming");

    private static final String TAG = SortBy.class.getSimpleName();

    private final int id;
    private final String value;
    private String displayName;

    SortBy(int id, String value) {
        this.value = value;
        this.displayName = value;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Sets the display name for each value to it's localized string.
     *
     * @param context Context to use for getting application resources.
     */
    public static void setLocalizedDisplayNames(Context context) {
        HashMap<String, String> displayNames = ResourceUtils.parseMapResource(context, R.xml.sort_options);
        for(String key : displayNames.keySet()) {
            try {
                SortBy sortBy = SortBy.valueOf(key);
                sortBy.displayName = displayNames.get(key);
            } catch(IllegalArgumentException ex) {
                Log.e(TAG, key + " not found in enum", ex);
            }

        }
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return value;
    }
}
