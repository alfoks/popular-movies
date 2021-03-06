package gr.alfoks.popularmovies.mvp.movies;

import butterknife.BindView;
import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseFragment;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.util.EndlessRecyclerViewScrollListener;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public final class MoviesFragment
    extends BaseFragment<MoviesContract.View, MoviesContract.Presenter>
    implements MoviesContract.View {
    private static final String KEY_SORT_BY = "SORT_BY";
    private static final String KEY_LAYOUT_MANAGER_STATE = "LM_STATE";

    @BindView(R.id.rcvMovies)
    RecyclerView rcvMovies;

    private MoviesAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private OnMovieClickedListener listener;

    private boolean stateRestored = false;

    public MoviesFragment() {
    }

    public static MoviesFragment newInstance(SortBy sortBy) {
        MoviesFragment moviesFragment = new MoviesFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_SORT_BY, sortBy);
        moviesFragment.setArguments(bundle);

        return moviesFragment;
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_movies;
    }

    @NonNull
    @Override
    protected MoviesContract.Presenter providePresenter() {
        PopularMoviesApplication app = (PopularMoviesApplication)getContext().getApplicationContext();
        return new MoviesPresenter(app.provideRepository());
    }

    @NonNull
    @Override
    protected MoviesContract.View getThis() {
        return this;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        rcvMovies.setItemAnimator(new DefaultItemAnimator());

        adapter = new MoviesAdapter(getContext(), (MoviesContract.ListPresenter)getPresenter());
        rcvMovies.setAdapter(adapter);

        scrollListener = createScrollListener((LinearLayoutManager)rcvMovies.getLayoutManager());
        rcvMovies.addOnScrollListener(scrollListener);

        if(savedInstanceState != null) {
            rcvMovies
                .getLayoutManager()
                .onRestoreInstanceState(
                    savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE)
                );

            stateRestored = true;
        }
    }

    public void setSortBy(SortBy sortBy) {
        if(!stateRestored) {
            getArguments().putSerializable(KEY_SORT_BY, sortBy);
            getPresenter().setSortBy(sortBy);
            getPresenter().loadMovies();
        }
        stateRestored = false;
    }

    @NonNull
    private EndlessRecyclerViewScrollListener createScrollListener(final LinearLayoutManager layoutManager) {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getPresenter().loadMovies();
            }
        };
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(
            KEY_LAYOUT_MANAGER_STATE,
            rcvMovies.getLayoutManager().onSaveInstanceState()
        );
    }

    @Override
    public void reset() {
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    @Override
    public void onErrorLoadingMovies(Throwable e) {
        //TODO: Show error
    }

    @Override
    public void showMovieDetails(Movie movie) {
        listener.onMovieClicked(movie.id);
    }

    @Override
    public void onDataChanged() {
        adapter.notifyDataSetChanged();
    }

    private SortBy getSortBy() {
        SortBy sortBy = (SortBy)getArguments().getSerializable(KEY_SORT_BY);
        return sortBy == null ? SortBy.POPULAR : sortBy;
    }

    public void onConnectivityChanged(boolean connectionOn) {
        getPresenter().onConnectivityChanged(connectionOn);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnMovieClickedListener) {
            listener = (OnMovieClickedListener)context;
        } else {
            listener = nullListener;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnMovieClickedListener {
        void onMovieClicked(long movieId);
    }

    private final OnMovieClickedListener nullListener = movieId -> {
    };
}
