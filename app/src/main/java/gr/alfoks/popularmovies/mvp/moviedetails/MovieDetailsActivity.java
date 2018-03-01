package gr.alfoks.popularmovies.mvp.moviedetails;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.util.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MovieDetailsActivity extends AppCompatActivity {
    @BindView(R.id.tlbMovieDetails)
    Toolbar tlbMovieDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        setupActionBar();
        attachFragment();
    }

    private void setupActionBar() {
        setSupportActionBar(tlbMovieDetails);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_movie);
        }
    }

    private void attachFragment() {
        Bundle bundle = Utils.getExtras(getIntent());
        long movieId = bundle.getLong(MovieDetailsFragment.KEY_MOVIE_ID);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.frgMovieDetails,
                MovieDetailsFragment.newInstance(movieId))
            .commit();
    }
}
