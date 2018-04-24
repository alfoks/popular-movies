package gr.alfoks.popularmovies.mvp.base;

import butterknife.ButterKnife;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment<V extends MvpView, P extends MvpPresenter<V>>
    extends Fragment {
    private P presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPresenter(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentResource(), container, false);
        ButterKnife.bind(this, view);

        presenter.attach(getThis());
        init(savedInstanceState);
        return view;
    }

    @Override
    public void onDetach() {
        presenter.detach();
        super.onDetach();
    }

    public void attachPresenter(P retainedPresenter) {
        if(retainedPresenter == null) {
            presenter = providePresenter();
        } else {
            presenter = retainedPresenter;
        }
        presenter.attach(getThis());
    }

    public P getPresenter() {
        return presenter;
    }

    /** Layout resource to be inflated */
    @LayoutRes
    protected abstract int getContentResource();
    @NonNull
    protected abstract P providePresenter();
    @NonNull
    protected abstract V getThis();
    protected abstract void init(@Nullable Bundle savedInstanceState);
    protected abstract void onConnectivityChanged(boolean connectionOn);
}
