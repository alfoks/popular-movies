package gr.alfoks.popularmovies.util;

import java.util.HashMap;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.XmlRes;
import android.util.Log;


import org.xmlpull.v1.XmlPullParser;

public class ResourceUtils {
    private static final String TAG = ResourceUtils.class.getSimpleName();

    @NonNull
    public static HashMap<String, String> parseMapResource(Context context, @XmlRes int hashMapResId) {
        HashMap<String, String> map = new HashMap<>();
        XmlResourceParser parser = context.getResources().getXml(hashMapResId);

        String key = null, value = null;

        try {
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(parser.getName().equals("key")) {
                        key = parser.getAttributeValue(null, "name");

                        if(key == null) {
                            parser.close();
                        }
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(parser.getName().equals("key")) {
                        map.put(key, value);
                        key = null;
                        value = null;
                    }
                } else if(eventType == XmlPullParser.TEXT) {
                    if(key != null) {
                        value = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch(Exception ex) {
            Log.e(TAG, "Could not parse xml", ex);
        }

        return map;
    }
}
