package gr.alfoks.popularmovies.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BaseMvpActivity<V extends MvpView, P extends MvpPresenter<V>>
    extends BaseActivity implements
    MvpView {

    private P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = providePresenter();
        presenter.attach(getThis());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    protected abstract P providePresenter();
    protected abstract V getThis();

    protected P getPresenter() {
        return presenter;
    }
}
