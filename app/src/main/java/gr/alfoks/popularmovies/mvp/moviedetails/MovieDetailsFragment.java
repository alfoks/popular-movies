package gr.alfoks.popularmovies.mvp.moviedetails;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseFragment;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.reviews.ReviewsAdapter;
import gr.alfoks.popularmovies.mvp.reviews.ReviewsContract;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public final class MovieDetailsFragment
    extends
    BaseFragment<MovieDetailsContract.View, MovieDetailsContract.Presenter>
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
    @BindView(R.id.rcvTrailers)
    RecyclerView rcvTrailers;
    @BindView(R.id.rcvReviews)
    RecyclerView rcvReviews;

    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;
    private OnSeeAllReviewsListener listener;

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

    @NonNull
    @Override
    protected MovieDetailsContract.Presenter providePresenter() {
        PopularMoviesApplication app = (PopularMoviesApplication)getContext().getApplicationContext();
        return new MovieDetailsPresenter(app.provideRepository());
    }

    @NonNull
    @Override
    protected MovieDetailsContract.View getThis() {
        return this;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        long movieId = getArguments().getLong(KEY_MOVIE_ID);
        initTrailersRecyclerView();
        initReviewsRecyclerView();

        getPresenter().loadMovie(movieId, savedInstanceState != null);
    }

    private void initTrailersRecyclerView() {
        trailersAdapter = new TrailersAdapter(getContext(), (TrailersContract.ListPresenter)getPresenter());
        initRecyclerView(rcvTrailers, trailersAdapter);
    }

    private void initReviewsRecyclerView() {
        reviewsAdapter = new ReviewsAdapter(getContext(), (ReviewsContract.ListPresenter)getPresenter());
        initRecyclerView(rcvReviews, reviewsAdapter);
    }

    private void initRecyclerView(RecyclerView view, RecyclerView.Adapter adapter) {
        view.setNestedScrollingEnabled(false);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setAdapter(adapter);
    }

    @Override
    public void onMovieLoaded(Movie movie) {
        trailersAdapter.notifyDataSetChanged();
        reviewsAdapter.notifyDataSetChanged();

        txtTitle.setText(movie.title);
        txtYear.setText(String.valueOf(movie.getReleaseYear()));
        txtDuration.setText(movie.getDuration());
        txtRating.setText(String.valueOf(movie.getRating()));
        setFavoriteButtonIcon(movie.favorite);
        txtOverview.setText(movie.overview);

        Picasso.with(getContext())
               .load(movie.getFullPosterPath())
               .placeholder(R.drawable.anim_loading)
               .error(R.drawable.ic_warning)
               .into(imgPoster);
    }

    private void setFavoriteButtonIcon(boolean favorite) {
        int icon = favorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border;
        btnFavorite.setImageResource(icon);
    }

    @OnClick(R.id.btnFavorite)
    void toggleFavorite() {
        getPresenter().toggleFavorite();
    }

    @Override
    public void onMovieUpdated(Movie movie) {
        setFavoriteButtonIcon(movie.favorite);
    }

    @Override
    public void playTrailer(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if(intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.msg_no_video_app, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showAllReviews(long movieId) {
        listener.onSeeAllReviews(movieId);
    }

    @Override
    public void onConnectivityChanged(boolean connectionOn) {
        getPresenter().onConnectivityChanged(connectionOn);
    }

    @OnClick(R.id.txtSeeAll)
    void onSeeAllClicked() {
        getPresenter().seeAllReviewsClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSeeAllReviewsListener) {
            listener = (OnSeeAllReviewsListener)context;
        } else {
            listener = nullListener;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSeeAllReviewsListener {
        void onSeeAllReviews(long movieId);
    }

    private final OnSeeAllReviewsListener nullListener = movieId -> {
    };
}
