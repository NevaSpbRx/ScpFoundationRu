package ru.dante.scpfoundation.ui.fragment;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.mvp.contract.RatedArticlesMvp;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class RatedArticlesFragment
        extends BaseArticlesListFragment<RatedArticlesMvp.View, RatedArticlesMvp.Presenter>
        implements RatedArticlesMvp.View {

    public static final String TAG = RatedArticlesFragment.class.getSimpleName();

    public static RatedArticlesFragment newInstance() {
        return new RatedArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected boolean isSwipeRefreshEnabled() {
        return true;
    }
}