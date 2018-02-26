package gr.alfoks.popularmovies.mvp.movies;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsActivity;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MoviesActivity extends AppCompatActivity implements
    MoviesFragment.OnMovieClickedListener {
    @BindView(R.id.tlbMovies)
    Toolbar tlbMovies;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        setupActionBar();
    }

    private void setupActionBar() {
        setSupportActionBar(tlbMovies);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_movie);
        }
    }

    @Override
    public void onMovieClicked(long movieId) {
        final Intent showMovieIntent = new Intent(this, MovieDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(MovieDetailsFragment.KEY_MOVIE_ID, movieId);
        showMovieIntent.putExtras(bundle);

        startActivity(showMovieIntent);
    }
}
