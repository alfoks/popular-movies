package gr.alfoks.popularmovies.mvp.base;

import butterknife.ButterKnife;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>>
    extends AppCompatActivity implements
    MvpView {

    private P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = providePresenter();
        setContentView(getContentResource());

        ButterKnife.bind(this);

        presenter.attach(getThis());
        init(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    /**
     * Get layout resource to be inflated
     */
    @LayoutRes
    protected abstract int getContentResource();
    protected abstract P providePresenter();
    protected abstract V getThis();
    protected abstract void init(@Nullable Bundle state);

    protected P getPresenter() {
        return presenter;
    }
}
