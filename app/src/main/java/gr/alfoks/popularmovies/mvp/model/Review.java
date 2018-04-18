package gr.alfoks.popularmovies.mvp.model;

import gr.alfoks.popularmovies.data.table.ReviewsTable;

import android.content.ContentValues;
import android.database.Cursor;

public class Review {
    public final String id;
    public final String author;
    public final String content;

    private Review(ReviewBuilder builder) {
        this.id = builder.id;
        this.author = builder.author;
        this.content = builder.content;
    }

    public static Review.ReviewBuilder builder() {
        return new Review.ReviewBuilder();
    }

    public ContentValues asValues(long movieId) {
        ContentValues values = new ContentValues();
        values.put(ReviewsTable.Columns.ID, id);
        values.put(ReviewsTable.Columns.MOVIE_ID, movieId);
        values.put(ReviewsTable.Columns.AUTHOR, author);
        values.put(ReviewsTable.Columns.CONTENT, content);

        return values;
    }

    public static class ReviewBuilder {
        private String id;
        private String author;
        private String content;

        public Review.ReviewBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public Review.ReviewBuilder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Review.ReviewBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public Review build() {
            return new Review(this);
        }

        public Review.ReviewBuilder from(Cursor c) {
            setId(c.getString(c.getColumnIndex(ReviewsTable.Columns.ID)));
            setAuthor(c.getString(c.getColumnIndex(ReviewsTable.Columns.AUTHOR)));
            setContent(c.getString(c.getColumnIndex(ReviewsTable.Columns.CONTENT)));

            return this;
        }
    }
}
