package ru.dante.scpfoundation.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.mvp.base.BaseArticlesListMvp;
import ru.dante.scpfoundation.ui.activity.ArticleActivity;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterListArticles;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterListArticlesWithSearch;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 *
 * @see <a href="http://stackoverflow.com/a/28732909/3212712">restore search view state</a>
 */
public abstract class BaseListArticlesWithSearchFragment
        <V extends BaseArticlesListMvp.View, P extends BaseArticlesListMvp.Presenter<V>>
        extends BaseArticlesListFragment<V, P> {

    private static final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";

    @BindView(R.id.searchFAB)
    protected FloatingActionButton mSearchFAB;

    private String mSearchQuery = "";
    private String mSavedQuery = "";
    private MenuItem menuItem;

    protected RecyclerAdapterListArticlesWithSearch mAdapter;

    @Override
    protected RecyclerAdapterListArticlesWithSearch getAdapter() {
        if (mAdapter == null) {
            mAdapter = new RecyclerAdapterListArticlesWithSearch();
        }
        return mAdapter;
    }

    @Override
    protected void initAdapter() {
        getAdapter().setArticleClickListener(new RecyclerAdapterListArticles.ArticleClickListener() {
            @Override
            public void onArticleClicked(Article article, int position) {
                ArticleActivity.startActivity(getActivity(), (ArrayList<String>) Article.getListOfUrls(getAdapter().getSortedData()), position);
            }

            @Override
            public void toggleReadenState(Article article) {
                mPresenter.toggleReadenState(article.url);
            }

            @Override
            public void toggleFavoriteState(Article article) {
                mPresenter.toggleFavoriteState(article.url);
            }

            @Override
            public void onOfflineClicked(Article article) {
                mPresenter.toggleOfflineState(article);
            }
        });
        getAdapter().sortArticles(mSearchQuery);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list_with_search;
    }

    @Override
    protected boolean isHasOptionsMenu() {
        return true;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_search;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(EXTRA_SEARCH_QUERY);
            mSavedQuery = mSearchQuery;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchView searchView = new SearchView(getActivity());
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_hint) + "</font>"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchQuery = newText;
                mAdapter.sortArticles(newText);
                return true;
            }
        });

        changeSearchViewTextColor(searchView);
        MenuItem search = menu.findItem(R.id.menuItemSearch);
        search.setActionView(searchView);

        menuItem = search;

        if (!TextUtils.isEmpty(mSearchQuery)) {
            searchView.post(() -> {
                searchView.onActionViewExpanded();
                searchView.setQuery(mSavedQuery, false);
            });
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        mSearchFAB.setOnClickListener(v -> ((SearchView) menuItem.getActionView()).onActionViewExpanded());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //fix crash by NPE
                if (mSearchFAB == null) {
                    return;
                }
                if (dy > 0 || dy < 0 && mSearchFAB.isShown()) {
                    mSearchFAB.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //fix crash by NPE
                if (mSearchFAB == null) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mSearchFAB.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    /**
     * Грязный хак исправления цвета текста
     */
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }
}