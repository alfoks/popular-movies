package gr.alfoks.popularmovies.data.source;

import gr.alfoks.popularmovies.data.table.MoviesTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class Utils {
    private static String KEY_CACHE_REFRESH_TIME = "cacheRefreshTime";

    static void setPageSize(Context context, int pageSize) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(MoviesTable.KEY_PAGE_SIZE, pageSize);
        editor.apply();
    }

    static int getPageSize(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(MoviesTable.KEY_PAGE_SIZE, TheMovieDbDataSource.DEFAULT_PAGE_SIZE);
    }

    static void setCacheRefreshTime(Context context, long refreshTime) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(KEY_CACHE_REFRESH_TIME, refreshTime);
        editor.apply();
    }

    static long getCacheRefreshTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(KEY_CACHE_REFRESH_TIME, 0);
    }

}
