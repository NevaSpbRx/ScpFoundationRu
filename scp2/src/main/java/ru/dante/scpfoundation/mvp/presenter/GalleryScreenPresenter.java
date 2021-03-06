package ru.dante.scpfoundation.mvp.presenter;

import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.VkImage;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.GalleryScreenMvp;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class GalleryScreenPresenter
        extends BaseDrawerPresenter<GalleryScreenMvp.View>
        implements GalleryScreenMvp.Presenter {

    private List<VkImage> mData;

    public GalleryScreenPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void onCreate() {
//        Timber.d("onCreate");
    }

    @Override
    public void onDestroy() {
        //nothing to do
    }

    @Override
    public void onNavigationItemClicked(int id) {
        //nothing to do
    }

    @Override
    protected void onReceiveUserFromDb() {
        super.onReceiveUserFromDb();
        getView().onGetUserFromDB(mUser);
    }

    @Override
    public void updateData() {
        mApiClient.getGallery()
                .flatMap(vkImages -> mDbProviderFactory.getDbProvider().saveImages(vkImages))
                .subscribe(
                        vkImages -> {
                            Timber.d("updateData onNext: %s", vkImages);
                        }, error -> {
                            Timber.e(error, "error while updateData");
                        }
                );
    }

    @Override
    public void getDataFromDb() {
        Timber.d("getCachedData");
        getView().showCenterProgress(true);
        getView().showEmptyPlaceholder(false);

        mDbProviderFactory.getDbProvider().getGalleryImages().subscribe(
                data -> {
                    Timber.d("getDataFromCache onNext: %s", data.size());

                    mData = data;

                    if (data.isEmpty()) {
                        //load from API
                        Timber.d("no data in DB, so load from api");
                        updateData();
                    } else {
                        getView().showCenterProgress(false);
                        getView().showEmptyPlaceholder(false);

                        getView().showData(data);
                    }
                }
        );
    }

    @Override
    public List<VkImage> getData() {
        return mData;
    }
}