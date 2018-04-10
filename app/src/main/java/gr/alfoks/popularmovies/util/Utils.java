package gr.alfoks.popularmovies.util;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class Utils {

    @NonNull
    public static Bundle getExtras(Intent intent) {
        if(intent == null || intent.getExtras() == null) return new Bundle();
        return intent.getExtras();
    }

    public static int parseInt(String value) {
        int[] parsedValue = new int[1];
        tryParse(value, parsedValue);
        return parsedValue[0];
    }

    public static boolean tryParse(String value, int[] parsedValue) {
        if(isInteger(value)) {
            parsedValue[0] = Integer.parseInt(value.trim());
            return true;
        }
        parsedValue[0] = 0;
        return false;
    }

    public static boolean isInteger(String value) {
        return isInteger(Locale.US, value);
    }

    public static boolean isInteger(Locale locale, String value) {
        if(value == null || value.trim().isEmpty()) return false;

        NumberFormat formatter = NumberFormat.getInstance(locale);
        formatter.setParseIntegerOnly(true);
        ParsePosition position = new ParsePosition(0);
        formatter.parse(value, position);
        return value.length() == position.getIndex();
    }
}
