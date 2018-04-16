package gr.alfoks.popularmovies.mvp.model;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import android.content.ContentValues;

public class Trailers {
    @SerializedName("results")
    private final List<Trailer> trailers;

    public Trailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public List<Trailer> getTrailers() {
        return Collections.unmodifiableList(trailers);
    }

    public ContentValues[] asValues(long movieId) {
        ContentValues[] values = new ContentValues[trailers.size()];
        for(int i = 0; i < trailers.size(); i++) {
            values[i] = trailers.get(i).asValues(movieId);
        }

        return values;

    }
}
