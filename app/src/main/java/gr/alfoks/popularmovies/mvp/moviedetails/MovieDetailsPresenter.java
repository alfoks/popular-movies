package gr.alfoks.popularmovies.mvp.moviedetails;

import gr.alfoks.popularmovies.data.source.Repository;
import gr.alfoks.popularmovies.mvp.base.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Movie;
import gr.alfoks.popularmovies.mvp.model.Review;
import gr.alfoks.popularmovies.mvp.model.Trailer;
import gr.alfoks.popularmovies.mvp.reviews.ReviewsContract;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

public final class MovieDetailsPresenter
    extends BasePresenter<MovieDetailsContract.View>
    implements MovieDetailsContract.Presenter,
    TrailersContract.ListPresenter, ReviewsContract.ListPresenter {
    @NonNull
    private final Repository repository;
    private Movie movie = Movie.builder().build();

    public MovieDetailsPresenter(@NonNull Repository repository) {
        this.repository = repository;
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadMovie(long movieId, boolean configChanged) {
        if(configChanged) {
            getView().onMovieLoaded(movie);
        } else {
            repository
                .loadMovie(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMovieLoaded, this::onError);
        }
    }

    private void onMovieLoaded(Movie movie) {
        this.movie = movie;
        getView().onMovieLoaded(movie);
    }

    @Override
    public void onError(Throwable e) {
        //Todo: Show error
    }

    @SuppressLint("CheckResult")
    @Override
    public void toggleFavorite() {
        repository
            .updateFavorite(movie.id, !movie.favorite)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(success -> onToggleFavoriteSuccess(), this::onError);
    }

    private void onToggleFavoriteSuccess() {
        movie = Movie.builder().from(movie).setFavorite(!movie.favorite).build();
        getView().onMovieUpdated(movie);
    }

    @Override
    public void seeAllReviewsClicked() {
        getView().showAllReviews(movie.id);
    }

    @Override
    public void onConnectivityChanged(boolean connectionOn) {
        //When we have internet again, load the movie, if not loaded already
        if(connectionOn && movie.isEmpty()) {
            loadMovie(movie.id, false);
        }
    }

    @Override
    protected MovieDetailsContract.View getView() {
        if(isViewAttached()) return super.getView();

        return nullView;
    }

    @Override
    public void onBindTrailerView(TrailersContract.View view, int position) {
        Trailer trailer = movie.trailers.getTrailers().get(position);
        view.setThumbnail(trailer.getThumbnailUrl());
    }

    @Override
    public int getTrailersCount() {
        return movie.trailers.getTrailers().size();
    }

    @Override
    public void onTrailerClicked(int position) {
        Trailer trailer = movie.trailers.getTrailers().get(position);
        getView().playTrailer(trailer.getUrl());
    }

    @Override
    public void onBindReviewView(ReviewsContract.ListItemView view, int position) {
        Review review = movie.reviews.getReviews().get(position);
        view.bindData(review);
    }

    @Override
    public int getReviewsCount() {
        return movie.reviews.getReviews().size();
    }

    private final MovieDetailsContract.View nullView = new MovieDetailsContract.View() {
        @Override
        public void onMovieLoaded(Movie movie) {
        }

        @Override
        public void onMovieUpdated(Movie movie) {
        }

        @Override
        public void playTrailer(String url) {
        }

        @Override
        public void showAllReviews(long movieId) {
        }
    };
}
