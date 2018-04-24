package gr.alfoks.popularmovies.mvp.moviedetails;

import gr.alfoks.popularmovies.mvp.base.MvpView;

public interface TrailersContract {

    interface ListPresenter {
        void onTrailerClicked(int position);
        void onBindTrailerView(View view, int position);
        int getTrailersCount();
    }

    interface View extends MvpView {
        void setThumbnail(String thumbnailUrl);
    }

}
