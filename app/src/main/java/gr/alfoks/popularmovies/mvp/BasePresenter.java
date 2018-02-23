package gr.alfoks.popularmovies.mvp;

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
    public boolean isAttached() {
        return view != null;
    }

    public V getView() {
        return view;
    }
}
