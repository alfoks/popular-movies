package gr.alfoks.popularmovies.mvp.moviedetails;

import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.util.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle bundle = Utils.getExtras(getIntent());
        long movieId = bundle.getLong(MovieDetailsFragment.KEY_MOVIE_ID);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.frgMovieDetails,
                MovieDetailsFragment.newInstance(movieId))
            .commit();
    }
}
