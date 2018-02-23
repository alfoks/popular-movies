package gr.alfoks.popularmovies.mvp.movies;

import butterknife.BindView;
import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.BaseFragment;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Movies;
import gr.alfoks.popularmovies.util.EndlessRecyclerViewScrollListener;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MoviesFragment extends BaseFragment<MoviesContract.Presenter>
    implements MoviesContract.View {

    @BindView(R.id.rcvMovies)
    RecyclerView rcvMovies;

    private MoviesAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    public MoviesFragment() {
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
    protected void init(@Nullable Bundle state) {
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rcvMovies.setLayoutManager(layoutManager);
        rcvMovies.setItemAnimator(new DefaultItemAnimator());

        adapter = new MoviesAdapter(getContext());
        rcvMovies.setAdapter(adapter);

        scrollListener = createScrollListener(layoutManager);
        rcvMovies.addOnScrollListener(scrollListener);

        getPresenter().resetList();
    }

    @Override
    public void onListReset() {
        adapter.reset();
        scrollListener.resetState();
        getPresenter().fetchNextMoviesPage();
    }

    @NonNull
    public EndlessRecyclerViewScrollListener createScrollListener(final GridLayoutManager layoutManager) {
        return new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getPresenter().fetchNextMoviesPage();
            }
        };
    }

    @Override
    public void onMoviesFetched(Movies movies) {
        for(Movie movie : movies.getMovies()) {
            adapter.addMovie(movie);
        }
    }
}
