package gr.alfoks.popularmovies.data;

import java.util.Arrays;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

class ContentUtils extends ContentUris {
    static String[] appendValueToSelectionArgs(String[] selectionArgs, String value) {
        String[] newSelectionArgs;

        if(selectionArgs == null) {
            newSelectionArgs = new String[1];
        } else {
            newSelectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
        }

        newSelectionArgs[newSelectionArgs.length - 1] = value;

        return newSelectionArgs;
    }

    static String appendFieldToSelection(String selection, String field) {
        String newSelection = field + "=?";
        if(TextUtils.isEmpty(selection)) {
            return newSelection;
        } else {
            return selection + " AND " + newSelection;
        }
    }

    /**
     * Used to force {@link android.database.sqlite.SQLiteDatabase#delete
     * SQLiteDatabase.delete} to return number of rows deleted when selection
     * parameter is null.
     *
     * @return the selection parameter if it's not null, "1" otherwise
     */
    static String normalizeSelection(String selection) {
        if(selection == null) return "1";

        return selection;
    }

    static String getStringId(Uri uri) {
        return String.valueOf(ContentUris.parseId(uri));
    }

    static void notifyChange(Context context, Uri uri) {
        if(context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }
}
