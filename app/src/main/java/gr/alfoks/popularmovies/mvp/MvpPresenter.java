package gr.alfoks.popularmovies.mvp;

public interface MvpPresenter<V extends MvpView> {
    void attach(V view);
    void detach();
    boolean isAttached();
}
