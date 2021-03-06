package ru.dante.scpfoundation.mvp.base;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import ru.dante.scpfoundation.db.model.User;

/**
 * Created by mohax on 14.01.2017.
 * <p>
 * for scp_ru
 */
public interface BaseMvp {
    interface View extends MvpView {
        void showError(Throwable throwable);
    }

    interface Presenter<V extends MvpView> extends MvpPresenter<V> {
        void onCreate();

        void onDestroy();

        void onUserLogined(User user);

        User getUser();
    }
}