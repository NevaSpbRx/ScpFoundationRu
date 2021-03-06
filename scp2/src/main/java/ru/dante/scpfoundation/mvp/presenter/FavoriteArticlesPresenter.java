package ru.dante.scpfoundation.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.FavoriteArticlesMvp;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class FavoriteArticlesPresenter
        extends BaseListArticlesPresenter<FavoriteArticlesMvp.View>
        implements FavoriteArticlesMvp.Presenter {

    public FavoriteArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider().getArticlesSortedAsync(Article.FIELD_IS_IN_FAVORITE, Sort.DESCENDING);
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        throw new IllegalStateException(MyApplication.getAppInstance().getString(R.string.not_implemented));
    }

    @Override
    protected Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset) {
        throw new IllegalStateException(MyApplication.getAppInstance().getString(R.string.not_implemented));
    }
}