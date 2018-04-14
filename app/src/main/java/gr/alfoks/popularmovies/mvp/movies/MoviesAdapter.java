package gr.alfoks.popularmovies.mvp.movies;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.model.Movie;
import io.reactivex.Observable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MoviesAdapter
    extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private final Context context;
    private final List<Movie> movies = new ArrayList<>();
    private final OnItemClickedListener listener;

    MoviesAdapter(Context context, OnItemClickedListener listener) {
        this.context = context;
        this.listener = listener;
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
        holder.bindData(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void addMovie(Movie movie) {
        movies.add(movie);
        notifyDataSetChanged();
    }

    void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
    }

    void reset() {
        movies.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
    public void removeMovie(long movieId) {
        Observable
            .fromIterable(movies)
            .filter(movie -> movie.id == movieId)
            .subscribe(movie -> {
                movies.remove(movie);
                notifyDataSetChanged();
            }, throwable -> {
            });
    }

    //TODO: Don't keep data in adapter

    /**
     * Expose movies. This method should be removed and the data not stored in
     * adapter at all.
     **/
    public List<Movie> getMovies() {
        return movies;
    }

    public interface OnItemClickedListener {
        void onItemClicked(Movie movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgPoster)
        ImageView imgPoster;
        @BindView(R.id.txtTitle)
        TextView txtTitle;

        private MovieViewHolder(android.view.View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imgPoster.setOnClickListener(createOnClickedListener());
        }

        private void bindData(Movie movie) {
            Picasso.with(context)
                   .load(movie.getFullPosterPath())
                   .placeholder(R.drawable.anim_loading)
                   .error(R.drawable.ic_warning)
                   .fit()
                   .into(imgPoster);
        }

        @NonNull
        private View.OnClickListener createOnClickedListener() {
            return v -> {
                if(listener != null) {
                    listener.onItemClicked(movies.get(getLayoutPosition()));
                }
            };
        }
    }
}
