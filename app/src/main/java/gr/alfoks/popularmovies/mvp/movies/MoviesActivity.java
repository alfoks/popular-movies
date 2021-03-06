package gr.alfoks.popularmovies.mvp.movies;

import butterknife.BindView;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseActivity;
import gr.alfoks.popularmovies.mvp.credits.CreditsActivity;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsActivity;
import gr.alfoks.popularmovies.mvp.moviedetails.MovieDetailsFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public final class MoviesActivity extends BaseActivity
    implements MoviesFragment.OnMovieClickedListener {
    private static final String KEY_SORT_BY = "SORT_BY";

    @BindView(R.id.tlbMain)
    Toolbar tlbMovies;

    private Spinner spnSortBy = null;

    @Override
    protected int getContentResource() {
        return R.layout.activity_movies;
    }

    private SortBy sortBy = SortBy.POPULAR;

    @Override
    protected void init(@Nullable Bundle state) {
        setupActionBar();
        setupFragment();
        attachPresenter();
    }

    private void setupActionBar() {
        setSupportActionBar(tlbMovies);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_movie);
        }
    }

    private void setupFragment() {
        if(getFragment() == null) attachFragment(sortBy);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.containsKey(KEY_SORT_BY)) {
            sortBy = SortBy.fromId(savedInstanceState.getInt(KEY_SORT_BY));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSortBySpinner(menu);

        return true;
    }

    private void setupSortBySpinner(Menu menu) {
        SortBy.setLocalizedDisplayNames(this);
        spnSortBy = (Spinner)menu.findItem(R.id.miSortBy).getActionView();
        final SortBySpinnerAdapter sortByAdapter = new SortBySpinnerAdapter(
            this,
            R.layout.item_sortby,
            SortBy.values()
        );

        //Remove spinner's arrow
        spnSortBy.setBackgroundColor(Color.TRANSPARENT);

        spnSortBy.setAdapter(sortByAdapter);

        //Initial selected item. On configuration change it will have been read
        //from restored state. Does not (and should not) trigger select listener
        //because it's set before setting the listener itself.
        setSelectedSortByItem(sortBy);
        spnSortBy.setOnItemSelectedListener(createOnItemSelectedListener(sortByAdapter));
    }

    private void setSelectedSortByItem(SortBy sortBy) {
        for(int i = 0; i < spnSortBy.getAdapter().getCount(); i++) {
            if(spnSortBy.getAdapter().getItem(i) == sortBy) {
                spnSortBy.setSelection(i);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.miCredits:
                showCredits();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCredits() {
        final Intent showLegalIntent = new Intent(this, CreditsActivity.class);
        startActivity(showLegalIntent);
    }

    @NonNull
    private AdapterView.OnItemSelectedListener createOnItemSelectedListener(final SortBySpinnerAdapter sortByAdapter) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = sortByAdapter.getItem(position);
                getFragment().setSortBy(sortBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private MoviesFragment getFragment() {
        return (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.frgPlaceholder);
    }

    private void attachFragment(SortBy sortBy) {
        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.frgPlaceholder, MoviesFragment.newInstance(sortBy))
            .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SORT_BY, sortBy.getId());
    }

    @Override
    public void onMovieClicked(long movieId) {
        final Intent showMovieIntent = new Intent(this, MovieDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(MovieDetailsFragment.KEY_MOVIE_ID, movieId);
        showMovieIntent.putExtras(bundle);

        startActivity(showMovieIntent);
    }

    @Override
    protected void onConnectivityChanged(boolean connectionOn) {
        MoviesFragment fragment = getFragment();
        if(fragment != null) {
            fragment.onConnectivityChanged(connectionOn);
        }
    }

    private void attachPresenter() {
        MoviesFragment fragment = getFragment();

        if(fragment != null) {
            MoviesContract.Presenter presenter = (MoviesContract.Presenter)getLastCustomNonConfigurationInstance();
            fragment.attachPresenter(presenter);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        MoviesFragment fragment = getFragment();
        if(fragment != null) {
            return fragment.getPresenter();
        } else {
            return super.onRetainCustomNonConfigurationInstance();
        }
    }
}
