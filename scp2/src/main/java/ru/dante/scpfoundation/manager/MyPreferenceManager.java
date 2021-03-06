package ru.dante.scpfoundation.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;

/**
 * Created by y.kuchanov on 22.12.16.
 * <p>
 * for scp_ru
 */
public class MyPreferenceManager {

    public interface Keys {
        String NIGHT_MODE = "NIGHT_MODE";
        String TEXT_SCALE_UI = "TEXT_SCALE_UI";
        String TEXT_SCALE_ARTICLE = "TEXT_SCALE_ARTICLE";
        String DESIGN_LIST_TYPE = "DESIGN_LIST_TYPE";

        String NOTIFICATION_IS_ON = "NOTIFICATION_IS_ON";
        String NOTIFICATION_PERIOD = "NOTIFICATION_PERIOD";
        String NOTIFICATION_VIBRATION_IS_ON = "NOTIFICATION_VIBRATION_IS_ON";
        String NOTIFICATION_LED_IS_ON = "NOTIFICATION_LED_IS_ON";
        String NOTIFICATION_SOUND_IS_ON = "NOTIFICATION_SOUND_IS_ON";

        String ADS_LAST_TIME_SHOWS = "ADS_LAST_TIME_SHOWS";
        String ADS_REWARDED_DESCRIPTION_IS_SHOWN = "ADS_REWARDED_DESCRIPTION_IS_SHOWN";
        String ADS_NUM_OF_INTERSTITIALS_SHOWN = "ADS_NUM_OF_INTERSTITIALS_SHOWN";

        String LICENCE_ACCEPTED = "LICENCE_ACCEPTED";
        String CUR_APP_VERSION = "CUR_APP_VERSION";
        String DESIGN_FONT_PATH = "DESIGN_FONT_PATH";
        String PACKAGE_INSTALLED = "PACKAGE_INSTALLED";
        String VK_GROUP_JOINED = "VK_GROUP_JOINED";
    }

    private SharedPreferences mPreferences;

    public MyPreferenceManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setIsNightMode(boolean isInNightMode) {
        mPreferences.edit().putBoolean(Keys.NIGHT_MODE, isInNightMode).apply();
    }

    public boolean isNightMode() {
        return mPreferences.getBoolean(Keys.NIGHT_MODE, false);
    }

    public void setUiTextScale(float uiTextScale) {
        mPreferences.edit().putFloat(Keys.TEXT_SCALE_UI, uiTextScale).apply();
    }

    public float getUiTextScale() {
        return mPreferences.getFloat(Keys.TEXT_SCALE_UI, .75f);
    }

    public float getArticleTextScale() {
        return mPreferences.getFloat(Keys.TEXT_SCALE_ARTICLE, .75f);
    }

    public void setArticleTextScale(float textScale) {
        mPreferences.edit().putFloat(Keys.TEXT_SCALE_ARTICLE, textScale).apply();
    }

    //design settings
    public boolean isDesignListNewEnabled() {
        return !mPreferences.getString(Keys.DESIGN_LIST_TYPE, SetttingsBottomSheetDialogFragment.ListItemType.MIDDLE).equals(SetttingsBottomSheetDialogFragment.ListItemType.MIN);
    }

    public void setListDesignType(@SetttingsBottomSheetDialogFragment.ListItemType String type) {
        mPreferences.edit().putString(Keys.DESIGN_LIST_TYPE, type).apply();
    }

    @SetttingsBottomSheetDialogFragment.ListItemType
    public String getListDesignType() {
        @SetttingsBottomSheetDialogFragment.ListItemType
        String type = mPreferences.getString(Keys.DESIGN_LIST_TYPE, SetttingsBottomSheetDialogFragment.ListItemType.MIDDLE);
        return type;
    }

    public void setFontPath(String type) {
        mPreferences.edit().putString(Keys.DESIGN_FONT_PATH, type).apply();
    }

    public String getFontPath() {
        return mPreferences.getString(Keys.DESIGN_FONT_PATH, "fonts/Roboto-Regular.ttf");
    }

    //new arts notifications
    public int getNotificationPeriodInMinutes() {
        return mPreferences.getInt(Keys.NOTIFICATION_PERIOD, 60);
    }

    public void setNotificationPeriodInMinutes(int minutes) {
        mPreferences.edit().putInt(Keys.NOTIFICATION_PERIOD, minutes).apply();
    }

    public boolean isNotificationEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_IS_ON, true);
    }

    public void setNotificationEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_IS_ON, enabled).apply();
    }

    public boolean isNotificationVibrationEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_VIBRATION_IS_ON, false);
    }

    public void setNotificationVibrationEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_VIBRATION_IS_ON, enabled).apply();
    }

    public boolean isNotificationLedEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_LED_IS_ON, false);
    }

    public void setNotificationLedEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_LED_IS_ON, enabled).apply();
    }

    public boolean isNotificationSoundEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_SOUND_IS_ON, false);
    }

    public void setNotificationSoundEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_SOUND_IS_ON, enabled).apply();
    }

    //ads
    public boolean isTimeToShowAds() {
        return System.currentTimeMillis() - getLastTimeAdsShows() >=
                FirebaseRemoteConfig.getInstance().getLong(Constants.Firebase.RemoteConfigKeys.PERIOD_BETWEEN_INTERSTITIAL_IN_MILLIS);
    }

    public void applyRewardFromAds() {
        setLastTimeAdsShows(System.currentTimeMillis() +
                FirebaseRemoteConfig.getInstance().getLong(Constants.Firebase.RemoteConfigKeys.REWARDED_VIDEO_COOLDOWN_IN_MILLIS));
    }

    public boolean isRewardedDescriptionShown() {
        return mPreferences.getBoolean(Keys.ADS_REWARDED_DESCRIPTION_IS_SHOWN, false);
    }

    public void setRewardedDescriptionIsNotShown(boolean isShown) {
        mPreferences.edit().putBoolean(Keys.ADS_REWARDED_DESCRIPTION_IS_SHOWN, isShown).apply();
    }

    public void setLastTimeAdsShows(long timeInMillis) {
        mPreferences.edit().putLong(Keys.ADS_LAST_TIME_SHOWS, timeInMillis).apply();
    }

    private long getLastTimeAdsShows() {
        long timeFromLastShow = mPreferences.getLong(Keys.ADS_LAST_TIME_SHOWS, 0);
        if (timeFromLastShow == 0) {
            setLastTimeAdsShows(System.currentTimeMillis());
        }
        return timeFromLastShow;
    }

    public void setNumOfInterstitialsShown(int numOfInterstitialsShown) {
        mPreferences.edit().putInt(Keys.ADS_NUM_OF_INTERSTITIALS_SHOWN, numOfInterstitialsShown).apply();
    }

    public int getNumOfInterstitialsShown() {
        return mPreferences.getInt(Keys.ADS_NUM_OF_INTERSTITIALS_SHOWN, 0);
    }

    public boolean isTimeToShowVideoInsteadOfInterstitial() {
        return getNumOfInterstitialsShown() >=
                FirebaseRemoteConfig.getInstance().getLong(Constants.Firebase.RemoteConfigKeys.NUM_OF_INTERSITIAL_BETWEEN_REWARDED);
    }

    //app installs
    public boolean isAppInstalledForPackage(String packageName) {
        return mPreferences.getBoolean(Keys.PACKAGE_INSTALLED + packageName, false);
    }

    public void setAppInstalledForPackage(String packageName) {
        mPreferences.edit().putBoolean(Keys.PACKAGE_INSTALLED + packageName, true).apply();
    }

    public void applyAwardForAppInstall() {
        setLastTimeAdsShows((System.currentTimeMillis() +
                FirebaseRemoteConfig.getInstance().getLong(Constants.Firebase.RemoteConfigKeys.APP_INSTALL_REWARD_IN_MILLIS)));
    }

    //vk groups join
    public boolean isVkGroupJoined(String id) {
        return mPreferences.getBoolean(Keys.VK_GROUP_JOINED + id, false);
    }

    public void setVkGroupJoined(String id) {
        mPreferences.edit().putBoolean(Keys.VK_GROUP_JOINED + id, true).apply();
    }

    public void applyAwardVkGroupJoined() {
        setLastTimeAdsShows((System.currentTimeMillis() +
                FirebaseRemoteConfig.getInstance().getLong(Constants.Firebase.RemoteConfigKeys.FREE_VK_GROUPS_JOIN_REWARD)));
    }

    //utils
    public boolean isLicenceAccepted() {
        return mPreferences.getBoolean(Keys.LICENCE_ACCEPTED, false);
    }

    public void setLicenceAccepted(boolean accepted) {
        mPreferences.edit().putBoolean(Keys.LICENCE_ACCEPTED, accepted).apply();
    }

    public int getCurAppVersion() {
        return mPreferences.getInt(Keys.CUR_APP_VERSION, 0);
    }

    public void setCurAppVersion(int versionCode) {
        mPreferences.edit().putInt(Keys.CUR_APP_VERSION, versionCode).apply();
    }
}