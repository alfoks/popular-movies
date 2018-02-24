package gr.alfoks.popularmovies.mvp.moviedetails;

import gr.alfoks.popularmovies.PopularMoviesApplication;
import gr.alfoks.popularmovies.R;
import gr.alfoks.popularmovies.mvp.BaseFragment;
import gr.alfoks.popularmovies.mvp.BasePresenter;
import gr.alfoks.popularmovies.mvp.MvpView;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class MovieDetailsFragment extends BaseFragment<BasePresenter<MvpView>>
    implements MovieDetailsContract.View {

    public MovieDetailsFragment() {
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_movie_details;
    }

    @Override
    protected BasePresenter<MvpView> providePresenter() {
        PopularMoviesApplication app = (PopularMoviesApplication)getContext().getApplicationContext();
        return app.provideMovieDetailsPresenter();
    }

    @Override
    protected void init(@Nullable Bundle state) {
    }
}
