package ru.dante.scpfoundation.mvp.contract;

import ru.dante.scpfoundation.mvp.base.BaseActivityMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface DrawerMvp {
    interface View extends BaseActivityMvp.View {
        /**
         *
         * @return true if need to show selected
         */
        boolean onNavigationItemClicked(int id);

        void onReceiveRandomUrl(String url);

        void showProgressDialog(boolean show);
    }

    interface Presenter<V extends View> extends BaseActivityMvp.Presenter<V> {
        void onNavigationItemClicked(int id);

        void getRandomArticleUrl();
    }
}