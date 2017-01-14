package ru.kuchanov.scp2.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.kuchanov.scp2.di.module.AppModule;
import ru.kuchanov.scp2.di.module.NetModule;
import ru.kuchanov.scp2.di.module.NotificationModule;
import ru.kuchanov.scp2.di.module.PresentersModule;
import ru.kuchanov.scp2.di.module.StorageModule;
import ru.kuchanov.scp2.receivers.ReceiverBoot;
import ru.kuchanov.scp2.receivers.ReceiverTimer;
import ru.kuchanov.scp2.service.DownloadAllService;
import ru.kuchanov.scp2.ui.activity.ArticleActivity;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterArticle;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterListArticles;
import ru.kuchanov.scp2.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scp2.ui.fragment.ArticleFragment;
import ru.kuchanov.scp2.ui.activity.MainActivity;
import ru.kuchanov.scp2.ui.fragment.FavoriteArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.Objects1ArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.Objects2ArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.Objects3ArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.ObjectsRuArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.OfflineArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.RatedArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.RecentArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.SiteSearchArticlesFragment;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
@Singleton
@Component(modules = {
        AppModule.class,
        StorageModule.class,
        PresentersModule.class,
        NetModule.class,
        NotificationModule.class
})
public interface AppComponent {
    void inject(MainActivity activity);

    void inject(ArticleActivity activity);

    void inject(ArticleFragment fragment);

    void inject(RecentArticlesFragment fragment);

    void inject(RatedArticlesFragment fragment);

    void inject(FavoriteArticlesFragment fragment);

    void inject(OfflineArticlesFragment fragment);

    void inject(Objects1ArticlesFragment fragment);

    void inject(Objects2ArticlesFragment fragment);

    void inject(Objects3ArticlesFragment fragment);

    void inject(ObjectsRuArticlesFragment fragment);

    void inject(SiteSearchArticlesFragment fragment);

    void inject(TextSizeDialogFragment dialogFragment);

    void inject(RecyclerAdapterListArticles adapterNewArticles);

    void inject(RecyclerAdapterArticle adapterNewArticles);

    void inject(DownloadAllService service);

    void inject(ReceiverTimer receiverTimer);

    void inject(ReceiverBoot receiverBoot);
}