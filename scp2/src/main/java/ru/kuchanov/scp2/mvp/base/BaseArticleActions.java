package ru.kuchanov.scp2.mvp.base;

import ru.kuchanov.scp2.db.model.Article;

/**
 * Created by mohax on 09.01.2017.
 * <p>
 * for scp_ru
 */
public interface BaseArticleActions {
    void toggleFavoriteState(String url);

    void toggleReadenState(String url);

    /**
     *  we need article as arg, as we should determine if we
     *  should start download or should delete text
     */
    void toggleOfflineState(Article article);
}