package gr.alfoks.popularmovies.mvp.movies;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.model.Movie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public final class MoviesAdapter
    extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private final Context context;
    private final MoviesContract.ListPresenter presenter;

    MoviesAdapter(Context context, MoviesContract.ListPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.item_movie, parent, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        presenter.onBindMovieView(holder, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getMoviesCount();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
        implements MoviesContract.ListItemView {
        @BindView(R.id.imgPoster)
        ImageView imgPoster;
        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.txtRating)
        TextView txtRating;

        private MovieViewHolder(android.view.View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.pnlMovies)
        void onMovieClicked() {
            presenter.movieClicked(getLayoutPosition());
        }

        @Override
        public void bindData(Movie movie) {
            txtTitle.setText(movie.getFullTitle());
            txtRating.setText(movie.getRating());

            Picasso.with(context)
                   .load(movie.getFullPosterPath())
                   .placeholder(R.drawable.anim_loading)
                   .error(android.R.color.transparent)
                   .fit()
                   .into(imgPoster);
        }
    }
}
