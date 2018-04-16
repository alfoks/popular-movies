package gr.alfoks.popularmovies.mvp.model;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Trailers {
    @SerializedName("results")
    private final List<Trailer> trailers;

    public Trailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public List<Trailer> getTrailers() {
        return Collections.unmodifiableList(trailers);
    }
}
