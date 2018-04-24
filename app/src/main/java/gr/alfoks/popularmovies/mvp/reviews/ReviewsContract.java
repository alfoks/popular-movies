package gr.alfoks.popularmovies.mvp.reviews;

import gr.alfoks.popularmovies.mvp.base.MvpPresenter;
import gr.alfoks.popularmovies.mvp.base.MvpView;
import gr.alfoks.popularmovies.mvp.model.Reviews;

public interface ReviewsContract {

    interface Presenter extends MvpPresenter<ReviewsContract.View> {
        void loadReviews();
    }

    interface View extends MvpView {
        void onReviewsLoaded(Reviews empty);
        void onErrorLoadingReviews(Throwable e);
        void reset();
    }

    interface ListPresenter {
        void onBindReviewView(ListItemView view, int position);
        int getReviewsCount();
    }

    interface ListItemView extends MvpView {
        void setAuthor(String author);
        void setContent(String content);
    }
}
