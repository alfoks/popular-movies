package gr.alfoks.popularmovies.mvp.base;

import butterknife.ButterKnife;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
        presenter = providePresenter();
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

    /** Layout resource to be inflated */
    @LayoutRes
    protected abstract int getContentResource();
    protected abstract P providePresenter();
    protected abstract V getThis();
    protected abstract void init(@Nullable Bundle state);

    protected P getPresenter() {
        return presenter;
    }
}
