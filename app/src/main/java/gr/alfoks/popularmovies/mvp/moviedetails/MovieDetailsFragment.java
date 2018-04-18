package gr.alfoks.popularmovies.mvp.moviedetails;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.base.BaseFragment;
import gr.alfoks.popularmovies.mvp.model.Movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    //Constraint layout views behave strangely on rotate, if a visual attribute
    //(text, background, ...) not set via code. So bind them all and
    //set attribute through code.
    @BindView(R.id.vwDivider)
    View vwDivider;
    @BindView(R.id.txtDummy)
    TextView txtDummy;
    @BindView(R.id.rcvTrailers)
    RecyclerView rcvTrailers;

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
    protected MovieDetailsContract.Presenter providePresenter() {
        PopularMoviesApplication app = (PopularMoviesApplication)getContext().getApplicationContext();
        return app.provideMovieDetailsPresenter();
    }

    @Override
    protected MovieDetailsContract.View getThis() {
        return this;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        long movieId = getArguments().getLong(KEY_MOVIE_ID);
        getPresenter().loadMovie(movieId);
    }

    @Override
    public void onMovieLoaded(Movie movie) {
        initTrailersRecyclerView();

        txtTitle.setText(movie.title);
        txtYear.setText(String.valueOf(movie.getReleaseYear()));
        txtDuration.setText(movie.getDuration());
        txtRating.setText(String.valueOf(movie.getRating()));

        //See comment on fields declaration about constraint layout
        txtTitle.setBackground(txtDummy.getBackground());
        vwDivider.setBackground(getContext().getResources().getDrawable(android.R.drawable.divider_horizontal_dark));

        setFavoriteButtonIcon(movie.favorite);

        txtOverview.setText(movie.overview);
        Picasso.with(getContext())
               .load(movie.getFullPosterPath())
               .placeholder(R.drawable.anim_loading)
               .error(R.drawable.ic_warning)
               .into(imgPoster);
    }

    private void initTrailersRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        );

        rcvTrailers.setLayoutManager(layoutManager);
        rcvTrailers.setItemAnimator(new DefaultItemAnimator());

        TrailersAdapter adapter = new TrailersAdapter(getContext(), getPresenter());
        rcvTrailers.setAdapter(adapter);
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

    public void onConnectivityChanged(boolean connectionOn) {
        getPresenter().onConnectivityChanged(connectionOn);
    }
}
