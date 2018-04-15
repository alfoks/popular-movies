package gr.alfoks.popularmovies.data.source;

public final class DataChange {
    public final DataChangeType type;
    public final Object data;

    DataChange(DataChangeType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
