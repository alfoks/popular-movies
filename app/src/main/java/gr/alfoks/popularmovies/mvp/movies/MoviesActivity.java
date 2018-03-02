package gr.alfoks.popularmovies.mvp.movies;

import butterknife.BindView;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseActivity;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsActivity;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class MoviesActivity extends BaseActivity
    implements MoviesFragment.OnMovieClickedListener {
    @BindView(R.id.tlbMain)
    Toolbar tlbMovies;

    @Override
    protected int getContentResource() {
        return R.layout.activity_movies;
    }

    @Override
    protected void init(@Nullable Bundle state) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSortBySpinner(menu);

        return true;
    }

    private void setupSortBySpinner(Menu menu) {
        Spinner spnSortBy = (Spinner)menu.findItem(R.id.spnSortBy).getActionView();
        final SortBySpinnerAdapter sortByAdapter = new SortBySpinnerAdapter(
            this,
            R.layout.item_sortby,
            SortBy.values()
        );

        spnSortBy.setAdapter(sortByAdapter);
        spnSortBy.setOnItemSelectedListener(createOnItemSelectedListener(sortByAdapter));
    }

    @NonNull
    private AdapterView.OnItemSelectedListener createOnItemSelectedListener(final SortBySpinnerAdapter sortByAdapter) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SortBy sortBy = sortByAdapter.getItem(position);

                MoviesFragment fragment = (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.frgPlaceholder);
                if(fragment == null) {
                    attachFragment(SortBy.POPULAR);
                } else {
                    fragment.setSortBy(sortBy);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void attachFragment(SortBy sortBy) {
        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.frgPlaceholder, MoviesFragment.newInstance(sortBy))
            .commit();
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
