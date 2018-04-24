package gr.alfoks.popularmovies.mvp.moviedetails;

import butterknife.BindView;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseActivity;
import gr.alfoks.popularmovies.util.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

public final class MovieDetailsActivity extends BaseActivity {
    @BindView(R.id.tlbMain)
    Toolbar tlbMovieDetails;

    @Override
    protected int getContentResource() {
        return R.layout.activity_movie_details;
    }

    @Override
    protected void init(@Nullable Bundle state) {
        setupActionBar();
        setupFragment();
    }

    private void setupActionBar() {
        setSupportActionBar(tlbMovieDetails);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_movie);
        }
    }

    private void setupFragment() {
        MovieDetailsFragment fragment = getFragment();
        if(fragment == null) attachFragment();
    }

    private MovieDetailsFragment getFragment() {
        return (MovieDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.frgPlaceholder);
    }

    private void attachFragment() {
        Bundle bundle = Utils.getExtras(getIntent());
        long movieId = bundle.getLong(MovieDetailsFragment.KEY_MOVIE_ID);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.frgPlaceholder, MovieDetailsFragment.newInstance(movieId))
            .commit();
    }

    @Override
    protected void onConnectivityChanged(boolean connectionOn) {
        MovieDetailsFragment fragment = (MovieDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.frgPlaceholder);
        if(fragment != null) {
            fragment.onConnectivityChanged(connectionOn);
        }
    }
}
