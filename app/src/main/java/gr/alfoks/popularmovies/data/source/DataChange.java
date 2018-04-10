package gr.alfoks.popularmovies.data.source;

public class DataChange {
    public final DataChangeType type;
    public final Object data;

    public DataChange(DataChangeType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
