package gr.alfoks.popularmovies.mvp.base;

public abstract class BasePresenter<V extends MvpView>
    implements MvpPresenter<V> {
    private V view;

    @Override
    public void attach(V view) {
        this.view = view;
    }

    @Override
    public void detach() {
        view = null;
    }

    protected boolean isViewAttached() {
        return view != null;
    }

    protected V getView() {
        return view;
    }
}
