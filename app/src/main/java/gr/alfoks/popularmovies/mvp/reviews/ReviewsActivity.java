package gr.alfoks.popularmovies.mvp.reviews;

import butterknife.BindView;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseActivity;
import gr.alfoks.popularmovies.util.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

public final class ReviewsActivity extends BaseActivity {
    @BindView(R.id.tlbMain)
    Toolbar tlbMovieDetails;

    @Override
    protected int getContentResource() {
        return R.layout.activity_movies;
    }

    @Override
    protected void init(@Nullable Bundle state) {
        setupActionBar();
        setupFragment();
        attachPresenter();
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
        if(getFragment() == null) attachFragment();
    }

    private ReviewsFragment getFragment() {
        return (ReviewsFragment)getSupportFragmentManager().findFragmentById(R.id.frgPlaceholder);
    }

    private void attachFragment() {
        Bundle bundle = Utils.getExtras(getIntent());
        long movieId = bundle.getLong(ReviewsFragment.KEY_MOVIE_ID);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.frgPlaceholder, ReviewsFragment.newInstance(movieId))
            .commit();
    }

    @Override
    protected void onConnectivityChanged(boolean connectionOn) {
        ReviewsFragment fragment = getFragment();

        if(fragment != null) {
            fragment.onConnectivityChanged(connectionOn);
        }
    }

    private void attachPresenter() {
        ReviewsFragment fragment = getFragment();

        if(fragment != null) {
            fragment.attachPresenter((ReviewsContract.Presenter)getLastCustomNonConfigurationInstance());
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        ReviewsFragment fragment = getFragment();
        if(fragment != null) {
            return fragment.getPresenter();
        } else {
            return super.onRetainCustomNonConfigurationInstance();
        }
    }
}
