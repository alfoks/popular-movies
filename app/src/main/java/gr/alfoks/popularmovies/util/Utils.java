package gr.alfoks.popularmovies.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class Utils {

    @NonNull
    public static Bundle getExtras(Intent intent) {
        if(intent == null || intent.getExtras() == null) return new Bundle();
        return intent.getExtras();
    }
}
