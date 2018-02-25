package gr.alfoks.popularmovies.mvp.movies;

import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsActivity;

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
    public void onMovieClicked(Movie movie) {
        final Intent intent = new Intent(this, MovieDetailsActivity.class);
        startActivity(intent);
    }
}
