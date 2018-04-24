package gr.alfoks.popularmovies.mvp.reviews;

import butterknife.BindView;
import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseFragment;
import gr.alfoks.popularmovies.mvp.model.Reviews;
import gr.alfoks.popularmovies.util.EndlessRecyclerViewScrollListener;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public final class ReviewsFragment
    extends BaseFragment<ReviewsContract.View, ReviewsContract.Presenter>
    implements ReviewsContract.View {
    private static final String KEY_LAYOUT_MANAGER_STATE = "LM_STATE";

    public static final String KEY_MOVIE_ID = "MOVIE_ID";

    @BindView(R.id.rcvReviews)
    RecyclerView rcvReviews;

    private ReviewsAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    public ReviewsFragment() {
    }

    public static ReviewsFragment newInstance(long movieId) {
        ReviewsFragment reviewsFragment = new ReviewsFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(KEY_MOVIE_ID, movieId);
        reviewsFragment.setArguments(bundle);

        return reviewsFragment;
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_reviews;
    }

    @NonNull
    @Override
    protected ReviewsContract.Presenter providePresenter() {
        long movieId = getArguments().getLong(KEY_MOVIE_ID);
        PopularMoviesApplication app = (PopularMoviesApplication)getContext().getApplicationContext();
        return new ReviewsPresenter(app.provideRepository(), movieId);
    }

    @NonNull
    @Override
    protected ReviewsContract.View getThis() {
        return this;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        rcvReviews.setItemAnimator(new DefaultItemAnimator());

        adapter = new ReviewsAdapter(getContext(), (ReviewsContract.ListPresenter)getPresenter());
        rcvReviews.setAdapter(adapter);

        scrollListener = createScrollListener((LinearLayoutManager)rcvReviews.getLayoutManager());
        rcvReviews.addOnScrollListener(scrollListener);

        if(savedInstanceState != null) {
            rcvReviews
                .getLayoutManager()
                .onRestoreInstanceState(
                    savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE)
                );
        } else {
            getPresenter().loadReviews();
        }
    }

    @NonNull
    private EndlessRecyclerViewScrollListener createScrollListener(final LinearLayoutManager layoutManager) {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getPresenter().loadReviews();
            }
        };
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putParcelable(
            KEY_LAYOUT_MANAGER_STATE,
            rcvReviews.getLayoutManager().onSaveInstanceState()
        );
    }

    @Override
    public void onErrorLoadingReviews(Throwable e) {
    }

    @Override
    public void reset() {
        scrollListener.resetState();
    }

    @Override
    public void onReviewsLoaded(Reviews reviews) {
        adapter.notifyDataSetChanged();
    }

    public void onConnectivityChanged(boolean connectionOn) {
        getPresenter().onConnectivityChanged(connectionOn);
    }
}
