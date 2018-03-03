package gr.alfoks.popularmovies.mvp.model;

import java.io.Serializable;
import java.util.HashMap;

import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.util.ResourceUtils;

import android.content.Context;
import android.util.Log;

public enum SortBy implements Serializable {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    NOW_PLAYING("now_playing"),
    UPCOMING("upcoming");

    private static final String TAG = SortBy.class.getSimpleName();

    private final String value;
    private String displayName;

    SortBy(String value) {
        this.value = value;
        this.displayName = value;
    }

    public String getDisplayName() {
        return displayName;
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

    @Override
    public String toString() {
        return value;
    }
}
