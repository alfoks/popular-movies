package gr.alfoks.popularmovies.mvp.movies;

import java.util.List;

import butterknife.BindView;
import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseFragment;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.mvp.model.SortBy;
import gr.alfoks.popularmovies.util.EndlessRecyclerViewScrollListener;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public final class MoviesFragment
    extends BaseFragment<MoviesContract.View, MoviesContract.Presenter>
    implements MoviesContract.View {
    private static final String KEY_SORT_BY = "SORT_BY";
    private static final String KEY_LAYOUT_MANAGER_STATE = "LM_STATE";
    private static final String KEY_MOVIES = "MOVIES";

    @BindView(R.id.rcvMovies)
    RecyclerView rcvMovies;

    private MoviesAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private OnMovieClickedListener listener;
    private GridLayoutManager layoutManager;

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

    @Override
    protected MoviesContract.Presenter providePresenter() {
        PopularMoviesApplication app = (PopularMoviesApplication)getContext().getApplicationContext();
        return app.provideMoviesPresenter();
    }

    @Override
    protected MoviesContract.View getThis() {
        return this;
    }

    @Override
    protected void init(@Nullable Bundle state) {
        layoutManager = new GridLayoutManager(getContext(), 2);
        rcvMovies.setLayoutManager(layoutManager);
        rcvMovies.setItemAnimator(new DefaultItemAnimator());

        adapter = new MoviesAdapter(getContext(), onItemClickedListener);
        rcvMovies.setAdapter(adapter);

        scrollListener = createScrollListener(layoutManager);
        rcvMovies.addOnScrollListener(scrollListener);

        if(state != null) {
            List<Movie> movies =
                Movie.listFromValuesArrayList(
                    state.getParcelableArrayList(KEY_MOVIES)
                );

            adapter.addMovies(movies);
            layoutManager.onRestoreInstanceState(state.getParcelable(KEY_LAYOUT_MANAGER_STATE));
            stateRestored = true;
        } else {
            setSortBy(getSortBy());
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
    private final MoviesAdapter.OnItemClickedListener onItemClickedListener =
        movie -> getPresenter().movieClicked(movie);

    @NonNull
    private EndlessRecyclerViewScrollListener createScrollListener(final GridLayoutManager layoutManager) {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getPresenter().loadMovies();
            }
        };
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putParcelable(KEY_LAYOUT_MANAGER_STATE, layoutManager.onSaveInstanceState());
        state.putParcelableArrayList(KEY_MOVIES, Movie.listAsValuesArrayList(adapter.getMovies()));
    }

    @Override
    public void reset() {
        adapter.reset();
        scrollListener.resetState();
    }

    @Override
    public void onMoviesLoaded(Movies movies) {
        for(Movie movie : movies.getMovies()) {
            adapter.addMovie(movie);
        }
    }

    @Override
    public void onErrorLoadingMovies(Throwable e) {
        //TODO: Show error
    }

    @Override
    public void onMovieClicked(Movie movie) {
        listener.onMovieClicked(movie.id);
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

    @Override
    public void onMovieRemoved(Movie movie) {
        adapter.removeMovie(movie.id);
    }

    private SortBy getSortBy() {
        SortBy sortBy = (SortBy)getArguments().getSerializable(KEY_SORT_BY);
        return sortBy == null ? SortBy.POPULAR : sortBy;
    }

    public void onConnectivityChanged(boolean connectionOn) {
        getPresenter().onConnectivityChanged(connectionOn);
    }

    public interface OnMovieClickedListener {
        void onMovieClicked(long movieId);
    }

    private final OnMovieClickedListener nullListener = movieId -> {
    };
}
