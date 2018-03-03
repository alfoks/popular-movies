package gr.alfoks.popularmovies.mvp.base;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private V view;

    @Override
    public void attach(V view) {
        this.view = view;
    }

    @Override
    public void detach() {
        view = null;
    }

    @Override
    public void onError(Throwable e) {

    }

    public boolean isViewAttached() {
        return view != null;
    }

    protected V getView() {
        return view;
    }
}
