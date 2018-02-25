package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsActivity;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MoviesActivity extends AppCompatActivity implements
    MoviesFragment.OnMovieClickedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
    }

    @Override
    public void onMovieClicked(long movieId) {
        final Intent intent = new Intent(this, MovieDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(MovieDetailsFragment.KEY_MOVIE_ID, movieId);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
