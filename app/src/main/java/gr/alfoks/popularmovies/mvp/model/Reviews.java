package gr.alfoks.popularmovies.mvp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import gr.alfoks.popularmovies.data.table.ReviewsTable;

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

    public ContentValues[] asValues(long movieId, int page) {
        long order = page * 10000000;
        ContentValues[] values = new ContentValues[reviews.size()];
        for(int i = 0; i < reviews.size(); i++) {
            values[i] = reviews.get(i).asValues(movieId);
            values[i].put(ReviewsTable.Columns.SORT_ORDER, order + i);
        }

        return values;
    }

    public Reviews mergeWith(Reviews reviews) {
        List<Review> reviewList = new ArrayList<>(this.reviews);
        reviewList.addAll(reviews.getReviews());
        return new Reviews(reviewList);
    }

    public static Reviews empty() {
        return new Reviews(new ArrayList<>());
    }
}
