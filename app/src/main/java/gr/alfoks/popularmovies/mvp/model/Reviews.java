package gr.alfoks.popularmovies.mvp.model;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import android.content.ContentValues;

public class Reviews {
    @SerializedName("results")
    private final List<Review> reviews;

    public Reviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public ContentValues[] asValues(long movieId) {
        ContentValues[] values = new ContentValues[reviews.size()];
        for(int i = 0; i < reviews.size(); i++) {
            values[i] = reviews.get(i).asValues(movieId);
        }

        return values;
    }
}
