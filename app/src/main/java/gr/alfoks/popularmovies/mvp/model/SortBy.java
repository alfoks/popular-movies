package gr.alfoks.popularmovies.mvp.model;

public enum SortBy {
    POPULAR("popular"),
    TOP_RATED("top_rated");

    private final String value;

    SortBy(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
