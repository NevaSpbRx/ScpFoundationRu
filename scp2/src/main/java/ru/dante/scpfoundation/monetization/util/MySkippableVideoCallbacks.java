package ru.dante.scpfoundation.monetization.util;

import com.appodeal.ads.SkippableVideoCallbacks;

import javax.inject.Inject;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import timber.log.Timber;

/**
 * Created by mohax on 12.03.2017.
 * <p>
 * for scp_ru
 */
public class MySkippableVideoCallbacks implements SkippableVideoCallbacks {

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    public MySkippableVideoCallbacks() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public void onSkippableVideoLoaded() {
        Timber.d("onSkippableVideoLoaded");
    }

    @Override
    public void onSkippableVideoFailedToLoad() {
        Timber.d("onSkippableVideoFailedToLoad");
    }

    @Override
    public void onSkippableVideoShown() {
        Timber.d("onSkippableVideoShown");
        mMyPreferenceManager.setLastTimeAdsShows(System.currentTimeMillis());
        mMyPreferenceManager.setNumOfInterstitialsShown(0);
    }

    @Override
    public void onSkippableVideoFinished() {
        Timber.d("onSkippableVideoFinished");
        mMyPreferenceManager.setLastTimeAdsShows(System.currentTimeMillis());
        mMyPreferenceManager.setNumOfInterstitialsShown(0);
    }

    @Override
    public void onSkippableVideoClosed(boolean finished) {
        Timber.d("onSkippableVideoClosed: %s", finished);
        mMyPreferenceManager.setLastTimeAdsShows(System.currentTimeMillis());
        mMyPreferenceManager.setNumOfInterstitialsShown(0);
    }
}