package gr.alfoks.popularmovies.mvp.movies;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.model.Movie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MoviesAdapter
    extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movies = new ArrayList<>();
    private OnItemClickedListener listener;

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
        Movie movie = movies.get(position);

        Picasso.with(context)
               .load(movie.getFullPosterPath())
               .placeholder(R.drawable.anim_loading)
               .fit()
               .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void addMovie(Movie movie) {
        movies.add(movie);
        notifyDataSetChanged();
    }

    void reset() {
        movies.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickedListener {
        void onItemClicked(Movie movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgPoster)
        ImageView imgPoster;

        private MovieViewHolder(android.view.View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imgPoster.setOnClickListener(createOnClickedListener());
        }

        @NonNull
        private View.OnClickListener createOnClickedListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onItemClicked(movies.get(getLayoutPosition()));
                    }
                }
            };
        }
    }
}
