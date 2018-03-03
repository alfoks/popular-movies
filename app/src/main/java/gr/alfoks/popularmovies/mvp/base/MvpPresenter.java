package gr.alfoks.popularmovies.mvp.base;

public interface MvpPresenter<V extends MvpView> {
    void attach(V view);
    void detach();
    void onError(Throwable e);
}
