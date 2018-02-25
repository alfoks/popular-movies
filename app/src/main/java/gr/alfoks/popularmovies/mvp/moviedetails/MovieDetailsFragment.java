package gr.alfoks.popularmovies.mvp.moviedetails;

import java.util.List;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.BaseFragment;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Trailer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieDetailsFragment extends BaseFragment<MovieDetailsPresenter>
    implements MovieDetailsContract.View {
    public static final String KEY_MOVIE_ID = "MOVIE_ID";

    @BindView(R.id.imgPoster)
    ImageView imgPoster;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.txtYear)
    TextView txtYear;
    @BindView(R.id.txtDuration)
    TextView txtDuration;
    @BindView(R.id.txtRating)
    TextView txtRating;
    @BindView(R.id.txtOverview)
    TextView txtOverview;
    @BindView(R.id.btnFavorite)
    ImageView btnFavorite;

    private long movieId;

    public MovieDetailsFragment() {
    }

    public static MovieDetailsFragment newInstance(long movieId) {
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(KEY_MOVIE_ID, movieId);
        movieDetailsFragment.setArguments(bundle);

        return movieDetailsFragment;
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_movie_details;
    }

    @Override
    protected MovieDetailsPresenter providePresenter() {
        PopularMoviesApplication app = (PopularMoviesApplication)getContext().getApplicationContext();
        return app.provideMovieDetailsPresenter();
    }

    @Override
    protected void init(@Nullable Bundle state) {
        movieId = getArguments().getLong(KEY_MOVIE_ID);
        getPresenter().loadMovie(movieId);
    }

    @Override
    public void onMovieLoaded(Movie movie) {
        txtTitle.setText(movie.title);
        txtYear.setText(String.valueOf(movie.getReleaseYear()));
        txtDuration.setText(movie.getDuration());
        txtRating.setText(String.valueOf(movie.getRating()));

        txtOverview.setText(movie.overview);
        Picasso.with(getContext())
               .load(movie.getFullPosterPath())
               .into(imgPoster);
    }

    @OnClick(R.id.btnFavorite)
    void markFavorite() {
        getPresenter().markFavorite(movieId);
    }

    @Override
    public void onFavored() {
        btnFavorite.setImageResource(R.drawable.ic_favorite);
    }

    @Override
    public void onTrailersLoaded(List<Trailer> trailers) {

    }

}
