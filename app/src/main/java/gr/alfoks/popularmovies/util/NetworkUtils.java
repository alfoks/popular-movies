package gr.alfoks.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils implements NetworkAvailabilityChecker {
    private static final NetworkUtils INSTANCE = new NetworkUtils();

    public static NetworkUtils getInstance() {
        return INSTANCE;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
