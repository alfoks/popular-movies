package gr.alfoks.popularmovies.data;

import java.util.Iterator;

import android.database.Cursor;
import android.support.annotation.NonNull;

public class CursorIterable implements Iterable<Cursor> {

    private Cursor cursor;

    public CursorIterable(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public Iterator<Cursor> iterator() {
        return RxCursorIterator.from(cursor);
    }

    static class RxCursorIterator implements Iterator<Cursor> {
        private final Cursor cursor;

        RxCursorIterator(Cursor cursor) {
            this.cursor = cursor;
        }

        static Iterator<Cursor> from(Cursor cursor) {
            return new RxCursorIterator(cursor);
        }

        @Override
        public boolean hasNext() {
            return !cursor.isClosed() && cursor.moveToNext();
        }

        @Override
        public Cursor next() {
            return cursor;
        }
    }
}
