package gr.alfoks.popularmovies.mvp.reviews;

import gr.alfoks.popularmovies.data.source.Repository;
import gr.alfoks.popularmovies.mvp.base.BasePresenter;
import gr.alfoks.popularmovies.mvp.model.Review;
import gr.alfoks.popularmovies.mvp.model.Reviews;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

public final class ReviewsPresenter extends BasePresenter<ReviewsContract.View>
    implements ReviewsContract.Presenter, ReviewsContract.ListPresenter {

    @NonNull
    private final Repository repository;
    private final long movieId;
    private int nextPage = 1;
    private Reviews reviews = Reviews.empty();

    ReviewsPresenter(@NonNull Repository repository, long movieId) {
        this.repository = repository;
        this.movieId = movieId;
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadReviews() {
        final Single<Reviews> reviewsSingle = repository.loadReviews(movieId, nextPage);

        reviewsSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onReviewsLoaded, this::onError);
    }

    private void onReviewsLoaded(Reviews reviews) {
        this.reviews = this.reviews.mergeWith(reviews);
        nextPage++;
        getView().onReviewsLoaded(reviews);
    }

    @Override
    public void onBindReviewView(ReviewsContract.ListItemView view, int position) {
        Review review = reviews.getReviews().get(position);
        view.setAuthor(review.author);
        view.setContent(review.content);
    }

    @Override
    public int getReviewsCount() {
        return reviews.getReviews().size();
    }

    @Override
    public void onError(Throwable e) {
        getView().onErrorLoadingReviews(e);
    }

    @Override
    public void onConnectivityChanged(boolean connectionOn) {
        loadReviews();
        getView().reset();
    }

    @Override
    protected ReviewsContract.View getView() {
        if(isViewAttached()) return super.getView();

        return nullView;
    }

    private final ReviewsContract.View nullView = new ReviewsContract.View() {

        @Override
        public void onReviewsLoaded(Reviews empty) {
        }

        @Override
        public void onErrorLoadingReviews(Throwable e) {
        }

        @Override
        public void reset() {
        }
    };
}
