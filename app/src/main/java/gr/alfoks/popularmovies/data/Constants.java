package gr.alfoks.popularmovies.data;

import gr.alfoks.popularmovies.BuildConfig;

import android.net.Uri;

public final class Constants {
    public static final String CONTENT_SCHEME = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + BuildConfig.CONTENT_AUTHORITY);
}
