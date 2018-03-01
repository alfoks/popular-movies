package gr.alfoks.popularmovies.mvp.movies;

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

public class MoviesFragment
    extends BaseFragment<MoviesContract.View, MoviesContract.Presenter>
    implements MoviesContract.View {
    private static final String KEY_SORT_BY = "SORT_BY";

    @BindView(R.id.rcvMovies)
    RecyclerView rcvMovies;

    private MoviesAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private OnMovieClickedListener listener;

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
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rcvMovies.setLayoutManager(layoutManager);
        rcvMovies.setItemAnimator(new DefaultItemAnimator());

        adapter = new MoviesAdapter(getContext(), onItemClickedListener);
        rcvMovies.setAdapter(adapter);

        scrollListener = createScrollListener(layoutManager);
        rcvMovies.addOnScrollListener(scrollListener);

        getPresenter().resetList();
    }

    @NonNull
    private final MoviesAdapter.OnItemClickedListener onItemClickedListener = new MoviesAdapter.OnItemClickedListener() {
        @Override
        public void onItemClicked(Movie movie) {
            getPresenter().showMovieDetails(movie);
        }
    };

    @Override
    public void onListReset() {
        adapter.reset();
        scrollListener.resetState();

        getPresenter().fetchNextMoviesPage(getSortBy());
    }

    @Override
    public void onShowMovieDetails(Movie movie) {
        listener.onMovieClicked(movie.id);
    }

    @NonNull
    private EndlessRecyclerViewScrollListener createScrollListener(final GridLayoutManager layoutManager) {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getPresenter().fetchNextMoviesPage(getSortBy());
            }
        };
    }

    @Override
    public void onMoviesFetched(Movies movies) {
        for(Movie movie : movies.getMovies()) {
            adapter.addMovie(movie);
        }
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

    private SortBy getSortBy() {
        SortBy sortBy = (SortBy)getArguments().getSerializable(KEY_SORT_BY);
        return sortBy == null ? SortBy.POPULAR : sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        getArguments().putSerializable(KEY_SORT_BY, sortBy);
        getPresenter().resetList();
    }

    public interface OnMovieClickedListener {
        void onMovieClicked(long movieId);
    }

    private final OnMovieClickedListener nullListener = new OnMovieClickedListener() {
        @Override
        public void onMovieClicked(long movieId) {
        }
    };
}
